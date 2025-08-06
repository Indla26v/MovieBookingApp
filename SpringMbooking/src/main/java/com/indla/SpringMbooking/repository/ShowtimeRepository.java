package com.indla.SpringMbooking.repository;

import com.indla.SpringMbooking.model.Movie;
import com.indla.SpringMbooking.model.Showtime;
import com.indla.SpringMbooking.model.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    List<Showtime> findByTheatreOrderByShowtimeAsc(Theatre theatre);

    List<Showtime> findByMovieOrderByShowtimeAsc(Movie movie);

    List<Showtime> findByMovie(Movie movie);

    List<Showtime> findByMovieAndShowtimeAfterOrderByShowtimeAsc(Movie movie, LocalDateTime dateTime);

    List<Showtime> findByTheatreAndShowtimeAfterOrderByShowtimeAsc(Theatre theatre, LocalDateTime dateTime);

    @Query("SELECT s FROM Showtime s WHERE s.showtime BETWEEN :startDate AND :endDate ORDER BY s.showtime ASC")
    List<Showtime> findShowtimesBetweenDates(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Showtime s WHERE s.theatre.id = :theatreId AND s.movie.id = :movieId AND s.showtime > :currentTime ORDER BY s.showtime ASC")
    List<Showtime> findUpcomingShowtimes(@Param("theatreId") Long theatreId,
                                         @Param("movieId") Long movieId,
                                         @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT s FROM Showtime s WHERE s.showtime > :currentTime ORDER BY s.showtime ASC")
    List<Showtime> findAllUpcomingShowtimes(@Param("currentTime") LocalDateTime currentTime);
}