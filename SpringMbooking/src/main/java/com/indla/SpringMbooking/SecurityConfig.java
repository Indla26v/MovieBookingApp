package com.indla.SpringMbooking;

import com.indla.SpringMbooking.config.CustomLoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
                        // Publicly accessible paths for login, registration, and static resources
                        .requestMatchers("/", "/login", "/register", "/auth/register",
                                "/auth/register/manager", "/auth/register/admin", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // Role-based access for admin paths
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // **UPDATED:** Role-based access for manager paths
                        // This now includes both the old path and the new /manager/** path
                        .requestMatchers("/auth/manager/**", "/manager/**").hasRole("MANAGER")

                        // Paths accessible by any authenticated user (USER, MANAGER, or ADMIN)
                        .requestMatchers("/bookings/**", "/dashboard", "/search").hasAnyRole("USER", "ADMIN", "MANAGER")
                        .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN", "MANAGER")

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler) // Use custom handler for role-based redirection
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