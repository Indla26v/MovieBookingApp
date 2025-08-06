package com.indla.SpringMbooking.repository;

import com.indla.SpringMbooking.model.Booking;
import com.indla.SpringMbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserOrderByBookingTimeDesc(User user);

    List<Booking> findByUserIdOrderByBookingTimeDesc(Long userId);

    List<Booking> findByShowtimeIdAndStatusNot(Long showtimeId, Booking.BookingStatus status);

    List<Booking> findByShowtimeId(Long showtimeId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.showtime.id = :showtimeId AND b.status != 'CANCELLED'")
    Long countConfirmedBookingsByShowtime(@Param("showtimeId") Long showtimeId);

    @Query("SELECT SUM(b.numberOfSeats) FROM Booking b WHERE b.showtime.id = :showtimeId AND b.status != 'CANCELLED'")
    Integer countBookedSeatsByShowtime(@Param("showtimeId") Long showtimeId);

    List<Booking> findByStatus(Booking.BookingStatus status);

    boolean existsByBookingReference(String bookingReference);
}