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

/**
 * Custom handler to redirect users to the appropriate dashboard
 * based on their role after a successful login.
 */
@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Get the roles of the authenticated user
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // Redirect based on the user's role
        if (roles.contains("ROLE_ADMIN")) {
            // Admins are sent to the admin panel
            response.sendRedirect("/admin");
        } else if (roles.contains("ROLE_MANAGER")) {
            // **UPDATED:** Managers are now sent to their new, dedicated dashboard
            response.sendRedirect("/manager/dashboard");
        } else {
            // All other users (e.g., ROLE_USER) are sent to the general user dashboard
            response.sendRedirect("/dashboard");
        }
    }
}