package com.indla.SpringMbooking.controller;

import com.indla.SpringMbooking.model.Booking;
import com.indla.SpringMbooking.model.Movie;
import com.indla.SpringMbooking.model.Showtime;
import com.indla.SpringMbooking.model.Theatre;
import com.indla.SpringMbooking.model.User;
import com.indla.SpringMbooking.repository.BookingRepository;
import com.indla.SpringMbooking.repository.MovieRepository;
import com.indla.SpringMbooking.repository.ShowtimeRepository;
import com.indla.SpringMbooking.repository.TheatreRepository;
import com.indla.SpringMbooking.repository.UserRepository;
import com.indla.SpringMbooking.service.BookingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private MovieRepository movieRepo;

    @Autowired
    private TheatreRepository theatreRepo;

    @Autowired
    private ShowtimeRepository showtimeRepo;

    @Autowired
    private UserRepository userRepo;

    // Show all movies for booking
    @GetMapping("/movies")
    public String showMoviesForBooking(Model model) {
        List<Movie> movies = movieRepo.findAll();
        model.addAttribute("movies", movies);
        return "booking-movies";
    }

    // Show theatres and showtimes for a specific movie
    @GetMapping("/movie/{movieId}/theatres")
    public String showTheatresForMovie(@PathVariable Long movieId, Model model) {
        Movie movie = movieRepo.findById(movieId).orElse(null);
        if (movie == null) {
            return "redirect:/bookings/movies";
        }

        List<Showtime> showtimes = showtimeRepo.findByMovieAndShowtimeAfterOrderByShowtimeAsc(
                movie, LocalDateTime.now());

        // Group showtimes by theatre
        var theatreShowtimes = showtimes.stream()
                .collect(Collectors.groupingBy(Showtime::getTheatre));

        model.addAttribute("movie", movie);
        model.addAttribute("theatreShowtimes", theatreShowtimes);
        return "booking-theatres";
    }

    // Show seat selection for a specific showtime
    @GetMapping("/showtime/{showtimeId}/seats")
    public String showSeatSelection(@PathVariable Long showtimeId, Model model, Principal principal) {
        Showtime showtime = showtimeRepo.findById(showtimeId).orElse(null);
        if (showtime == null || showtime.getShowtime().isBefore(LocalDateTime.now())) {
            return "redirect:/bookings/movies";
        }

        // Get booked seats for this showtime
        List<String> bookedSeats = bookingService.getBookedSeats(showtimeId);

        model.addAttribute("showtime", showtime);
        model.addAttribute("bookedSeats", bookedSeats);
        model.addAttribute("seatPrice", 150.0); // Base price per seat
        return "seat-selection";
    }

    // Process booking
    @PostMapping("/confirm")
    public String confirmBooking(@RequestParam Long showtimeId,
                                 @RequestParam String selectedSeats,
                                 @RequestParam Double totalAmount,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userRepo.findByUsername(principal.getName()).orElse(null);
            Showtime showtime = showtimeRepo.findById(showtimeId).orElse(null);

            if (user == null || showtime == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid booking details!");
                return "redirect:/bookings/movies";
            }

            // Validate seats are still available
            List<String> requestedSeats = List.of(selectedSeats.split(","));
            List<String> bookedSeats = bookingService.getBookedSeats(showtimeId);

            for (String seat : requestedSeats) {
                if (bookedSeats.contains(seat.trim())) {
                    redirectAttributes.addFlashAttribute("error", "Some seats are no longer available!");
                    return "redirect:/bookings/showtime/" + showtimeId + "/seats";
                }
            }

            // Create booking
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setShowtime(showtime);
            booking.setNumberOfSeats(requestedSeats.size());
            booking.setSeatNumbers(selectedSeats);
            booking.setTotalAmount(totalAmount);
            booking.setBookingReference("BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            bookingRepo.save(booking);

            redirectAttributes.addFlashAttribute("success", "Booking confirmed! Reference: " + booking.getBookingReference());
            return "redirect:/bookings/confirmation/" + booking.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Booking failed: " + e.getMessage());
            return "redirect:/bookings/showtime/" + showtimeId + "/seats";
        }
    }

    // Show booking confirmation
    @GetMapping("/confirmation/{bookingId}")
    public String showBookingConfirmation(@PathVariable Long bookingId, Model model, Principal principal) {
        Booking booking = bookingRepo.findById(bookingId).orElse(null);

        if (booking == null || !booking.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/bookings/movies";
        }

        model.addAttribute("booking", booking);
        return "booking-confirmation";
    }

    // Show user's booking history
    @GetMapping("/history")
    public String showBookingHistory(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        List<Booking> bookings = bookingRepo.findByUserOrderByBookingTimeDesc(user);
        model.addAttribute("bookings", bookings);
        return "booking-history";
    }

    // Cancel booking
    @PostMapping("/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId, Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userRepo.findByUsername(principal.getName()).orElse(null);
            Booking booking = bookingRepo.findById(bookingId).orElse(null);

            if (booking == null || !booking.getUser().equals(user)) {
                redirectAttributes.addFlashAttribute("error", "Booking not found!");
                return "redirect:/bookings/history";
            }

            // Check if showtime is at least 2 hours away
            if (booking.getShowtime().getShowtime().isBefore(LocalDateTime.now().plusHours(2))) {
                redirectAttributes.addFlashAttribute("error", "Cannot cancel booking less than 2 hours before showtime!");
                return "redirect:/bookings/history";
            }

            booking.setStatus(Booking.BookingStatus.CANCELLED);
            bookingRepo.save(booking);

            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error cancelling booking: " + e.getMessage());
        }

        return "redirect:/bookings/history";
    }
}