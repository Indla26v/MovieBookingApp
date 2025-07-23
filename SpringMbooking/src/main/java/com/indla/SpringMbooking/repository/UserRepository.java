package com.indla.SpringMbooking.repository;

import com.indla.SpringMbooking.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);



    boolean existsByUsernameOrEmail(String username, String email);
}
