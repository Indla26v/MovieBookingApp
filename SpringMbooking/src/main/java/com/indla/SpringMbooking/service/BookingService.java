package com.indla.SpringMbooking.service;

import com.indla.SpringMbooking.model.Booking;
import com.indla.SpringMbooking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public List<String> getBookedSeats(Long showtimeId) {
        List<Booking> bookings = bookingRepository.findByShowtimeIdAndStatusNot(
                showtimeId, Booking.BookingStatus.CANCELLED);

        List<String> bookedSeats = new ArrayList<>();
        for (Booking booking : bookings) {
            String[] seats = booking.getSeatNumbers().split(",");
            for (String seat : seats) {
                bookedSeats.add(seat.trim());
            }
        }
        return bookedSeats;
    }

    public boolean areSeatsAvailable(Long showtimeId, List<String> requestedSeats) {
        List<String> bookedSeats = getBookedSeats(showtimeId);
        return bookedSeats.stream().noneMatch(requestedSeats::contains);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingTimeDesc(userId);
    }

    public Booking findById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }

    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    // Generate seat layout for a theatre (simplified - can be enhanced)
    public List<List<String>> generateSeatLayout() {
        List<List<String>> seatLayout = new ArrayList<>();

        // Create rows A to J with 10 seats each
        for (char row = 'A'; row <= 'J'; row++) {
            List<String> rowSeats = new ArrayList<>();
            for (int seat = 1; seat <= 10; seat++) {
                rowSeats.add(row + String.valueOf(seat));
            }
            seatLayout.add(rowSeats);
        }

        return seatLayout;
    }
}