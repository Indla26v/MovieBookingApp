package com.indla.SpringMbooking.repository;

import com.indla.SpringMbooking.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Add custom methods if needed
}
