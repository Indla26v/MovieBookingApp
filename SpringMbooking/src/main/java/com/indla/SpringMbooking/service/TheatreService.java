package com.indla.SpringMbooking.service;

import com.indla.SpringMbooking.dto.TheatreDto;
import com.indla.SpringMbooking.model.Showtime;
import com.indla.SpringMbooking.model.Theatre;
import com.indla.SpringMbooking.repository.ShowtimeRepository;
import com.indla.SpringMbooking.repository.TheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TheatreService {

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    public List<TheatreDto> searchTheatres(String location) {
        List<Theatre> theatres = theatreRepository.findByLocationContainingIgnoreCase(location);
        return theatres.stream().map(theatre -> {
            List<Showtime> showtimes = showtimeRepository.findByTheatreOrderByShowtimeAsc(theatre);
            Showtime nextShowtime = showtimes.stream()
                    .filter(s -> s.getShowtime().isAfter(LocalDateTime.now()))
                    .findFirst().orElse(null);

            String currentMovie = (nextShowtime != null) ? nextShowtime.getMovie().getTitle() : "N/A";
            Double rating = (nextShowtime != null) ? nextShowtime.getMovie().getRating() : 0.0;

            return new TheatreDto(
                    theatre.getId(),
                    theatre.getName(),
                    theatre.getLocation(),
                    currentMovie,
                    rating,
                    (nextShowtime != null) ? nextShowtime.getShowtime().toString() : "N/A"
            );
        }).collect(Collectors.toList());
    }
    public List<TheatreDto> getNearbyTheatres() {
        // Return all for now
        return theatreRepository.findAll()
                .stream()
                .map(t -> new TheatreDto(t.getId(), t.getName(), t.getLocation(), null, null, null))
                .collect(Collectors.toList());
    }

}
