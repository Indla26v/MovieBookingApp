package com.indla.SpringMbooking;

import com.indla.SpringMbooking.config.CustomLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomLoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible paths
                        .requestMatchers("/", "/login", "/register", "/auth/register", "/auth/register/manager", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // Role-based access for /auth paths
                        .requestMatchers("/auth/admin/**").hasRole("ADMIN")
                        .requestMatchers("/auth/manager/**").hasRole("MANAGER")
                        .requestMatchers("/auth/register/admin").hasRole("ADMIN") // <-- NEW: Admin registration is admin-only

                        // Paths accessible by any relevant authenticated role
                        .requestMatchers("/dashboard", "/bookings/**").hasAnyRole("USER", "ADMIN", "MANAGER")

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}