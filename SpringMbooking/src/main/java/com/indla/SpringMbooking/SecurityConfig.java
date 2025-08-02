package com.indla.SpringMbooking;

import com.indla.SpringMbooking.config.CustomLoginSuccessHandler;
import com.indla.SpringMbooking.service.CustomOAuth2UserService;
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

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible paths
                        .requestMatchers("/", "/login", "/register", "/auth/register", "/auth/register/manager", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // OAuth2 related endpoints must be public for the flow to work
                        .requestMatchers("/oauth2/**", "/login/oauth2/code/**").permitAll()

                        // Role-based access for admin paths
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN") // Updated to secure all admin routes

                        // Role-based access for manager paths
                        .requestMatchers("/auth/manager/**").hasRole("MANAGER")

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
                .oauth2Login(oauth2 -> {
                    oauth2.loginPage("/login");
                    oauth2.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService));
                    oauth2.successHandler(loginSuccessHandler);
                })
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
