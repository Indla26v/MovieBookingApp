package com.indla.SpringMbooking.service;

import com.indla.SpringMbooking.dto.TheatreDto;
import com.indla.SpringMbooking.model.Showtime;
import com.indla.SpringMbooking.model.Theatre;
import com.indla.SpringMbooking.model.User;
import com.indla.SpringMbooking.repository.ShowtimeRepository;
import com.indla.SpringMbooking.repository.TheatreRepository;
import com.indla.SpringMbooking.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    public List<TheatreDto> searchTheatres(String location) {
        List<Theatre> theatres = theatreRepository.findByLocationContainingIgnoreCase(location);
        return convertToTheatreDto(theatres);
    }

    public List<TheatreDto> getNearbyTheatres() {
        List<Theatre> theatres = theatreRepository.findAll();
        return convertToTheatreDto(theatres);
    }

    public List<TheatreDto> getAllTheatres() {
        return getNearbyTheatres();
    }

    private List<TheatreDto> convertToTheatreDto(List<Theatre> theatres) {
        return theatres.stream().map(theatre -> {
            List<Showtime> showtimes = showtimeRepository.findByTheatreAndShowtimeAfterOrderByShowtimeAsc(
                    theatre, LocalDateTime.now());

            Showtime nextShowtime = showtimes.stream().findFirst().orElse(null);

            String currentMovie = (nextShowtime != null && nextShowtime.getMovie() != null)
                    ? nextShowtime.getMovie().getTitle() : "No shows scheduled";
            Double rating = (nextShowtime != null && nextShowtime.getMovie() != null)
                    ? nextShowtime.getMovie().getRating() : 0.0;
            String nextShowtimeStr = (nextShowtime != null)
                    ? nextShowtime.getShowtime().toString() : "No upcoming shows";

            return new TheatreDto(
                    theatre.getId(),
                    theatre.getName(),
                    theatre.getLocation(),
                    currentMovie,
                    rating,
                    nextShowtimeStr
            );
        }).collect(Collectors.toList());
    }

    public List<Theatre> getAllTheatreEntities() {
        return theatreRepository.findAll();
    }

    public Theatre findById(Long id) {
        return theatreRepository.findById(id).orElse(null);
    }

    public void assignTheatresToManager(Long managerId, List<Long> theatreIds) {
        User manager = userRepository.findById(managerId).orElse(null);
        if (manager == null) {
            throw new RuntimeException("Manager not found");
        }

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

    public Theatre save(Theatre theatre) {
        return theatreRepository.save(theatre);
    }

    public void deleteById(Long id) {
        theatreRepository.deleteById(id);
    }

    public List<Theatre> findByManager(User manager) {
        return theatreRepository.findByManager(manager);
    }

    public boolean existsById(Long id) {
        return theatreRepository.existsById(id);
    }

    public long count() {
        return theatreRepository.count();
    }
}