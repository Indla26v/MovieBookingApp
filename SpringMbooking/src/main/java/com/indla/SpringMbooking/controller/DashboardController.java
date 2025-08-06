package com.indla.SpringMbooking.controller;

import com.indla.SpringMbooking.dto.TheatreDto;
import com.indla.SpringMbooking.model.Movie;
import com.indla.SpringMbooking.model.Showtime;
import com.indla.SpringMbooking.model.User;
import com.indla.SpringMbooking.repository.BookingRepository;
import com.indla.SpringMbooking.repository.MovieRepository;
import com.indla.SpringMbooking.repository.ShowtimeRepository;
import com.indla.SpringMbooking.repository.UserRepository;
import com.indla.SpringMbooking.service.TheatreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private TheatreService theatreService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        model.addAttribute("username", username);
        model.addAttribute("user", user);

        // Get current movies with upcoming showtimes
        List<Movie> currentMovies = movieRepository.findAll().stream()
                .filter(movie -> {
                    List<Showtime> upcomingShowtimes = showtimeRepository.findByMovieAndShowtimeAfterOrderByShowtimeAsc(
                            movie, LocalDateTime.now());
                    return !upcomingShowtimes.isEmpty();
                })
                .collect(Collectors.toList());

        model.addAttribute("currentMovies", currentMovies);

        // Get nearby theatres with enhanced information
        List<TheatreDto> theatres = theatreService.getNearbyTheatres();
        model.addAttribute("theatres", theatres);

        // Get upcoming showtimes (next 7 days)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = now.plusDays(7);
        List<Showtime> upcomingShowtimes = showtimeRepository.findShowtimesBetweenDates(now, nextWeek)
                .stream()
                .limit(10) // Limit to 10 upcoming shows
                .collect(Collectors.toList());
        model.addAttribute("upcomingShowtimes", upcomingShowtimes);

        // User statistics if available
        if (user != null) {
            long userBookingsCount = bookingRepository.findByUserOrderByBookingTimeDesc(user).size();
            model.addAttribute("userBookingsCount", userBookingsCount);
        }

        return "dashboard";
    }

    @GetMapping("/search")
    public String showSearchPage(Model model) {
        List<Movie> allMovies = movieRepository.findAll();
        List<TheatreDto> allTheatres = theatreService.getAllTheatres();

        model.addAttribute("movies", allMovies);
        model.addAttribute("theatres", allTheatres);

        return "search";
    }
}