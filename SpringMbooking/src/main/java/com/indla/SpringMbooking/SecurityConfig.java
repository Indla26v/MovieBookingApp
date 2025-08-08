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
                        .requestMatchers("/", "/login", "/register", "/auth/register",
                                "/auth/register/manager", "/auth/register/admin", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()

                        // Role-based access for admin paths
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")

                        // Role-based access for manager paths
                        .requestMatchers("/auth/manager/**").hasRole("MANAGER")

                        // Booking paths accessible by authenticated users
                        .requestMatchers("/bookings/**").hasAnyRole("USER", "ADMIN", "MANAGER")

                        // Dashboard accessible by any authenticated user
                        .requestMatchers("/dashboard", "/search").hasAnyRole("USER", "ADMIN", "MANAGER")

                        // API endpoints
                        .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN", "MANAGER")
                        .requestMatchers("/auth/admin/**").hasRole("ADMIN")

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/login?access-denied=true")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}