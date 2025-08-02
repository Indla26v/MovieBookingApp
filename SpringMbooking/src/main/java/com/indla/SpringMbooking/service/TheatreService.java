package com.indla.SpringMbooking.service;

import com.indla.SpringMbooking.dto.TheatreDto;
import com.indla.SpringMbooking.model.Showtime;
import com.indla.SpringMbooking.model.Theatre;
import com.indla.SpringMbooking.model.User;
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

            String currentMovie = (nextShowtime != null && nextShowtime.getMovie() != null) ? nextShowtime.getMovie().getTitle() : "N/A";
            Double rating = (nextShowtime != null && nextShowtime.getMovie() != null) ? nextShowtime.getMovie().getRating() : 0.0;

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
        return theatreRepository.findAll()
                .stream()
                .map(t -> new TheatreDto(t.getId(), t.getName(), t.getLocation(), null, null, null))
                .collect(Collectors.toList());
    }

    public List<Theatre> getAllTheatres() {
        return theatreRepository.findAll();
    }

    public void assignTheatresToManager(Long managerId, List<Long> theatreIds) {
        User manager = new User();
        manager.setId(managerId);

        // First, unassign all theatres from this manager
        List<Theatre> currentTheatres = theatreRepository.findByManager(manager);
        for (Theatre theatre : currentTheatres) {
            theatre.setManager(null);
            theatreRepository.save(theatre);
        }

        // Then, assign the new list of theatres
        if (theatreIds != null && !theatreIds.isEmpty()) {
            List<Theatre> newTheatres = theatreRepository.findAllById(theatreIds);
            for (Theatre theatre : newTheatres) {
                theatre.setManager(manager);
                theatreRepository.save(theatre);
            }
        }
    }
}
