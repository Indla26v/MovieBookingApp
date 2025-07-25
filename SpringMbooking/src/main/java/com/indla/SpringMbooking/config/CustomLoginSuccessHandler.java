package com.indla.SpringMbooking.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // --- ADD THIS LOGGING ---
        System.out.println("DEBUG: Login successful for user: " + authentication.getName());
        System.out.println("DEBUG: User's authorities (roles): " + roles);
        // --- END LOGGING ---

        if (roles.contains("ROLE_ADMIN")) {
            // --- ADD THIS LOGGING ---
            System.out.println("DEBUG: Redirecting ADMIN to: /auth/admin/movies");
            // --- END LOGGING ---
            response.sendRedirect("/auth/admin/movies");
        } else if (roles.contains("ROLE_MANAGER")) {
            // --- ADD THIS LOGGING ---
            System.out.println("DEBUG: Redirecting MANAGER to: /auth/manager/assign");
            // --- END LOGGING ---
            response.sendRedirect("/auth/manager/assign");
        } else {
            // --- ADD THIS LOGGING ---
            System.out.println("DEBUG: Redirecting USER (or unhandled role) to: /dashboard");
            // --- END LOGGING ---
            response.sendRedirect("/dashboard");
        }
    }
}