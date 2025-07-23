package com.indla.SpringMbooking.repository;

import com.indla.SpringMbooking.model.Theatre;
import com.indla.SpringMbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    List<Theatre> findByManager(User manager);


    List<Theatre> findByLocationContainingIgnoreCase(String location);
}
