package com.indla.SpringMbooking.service;

import com.indla.SpringMbooking.model.User;
import com.indla.SpringMbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserRepository userRepository;

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest); // Fetches user info from Google

        String email = oauth2User.getAttribute("email");
        String username = oauth2User.getAttribute("name"); // Google's 'name' attribute is usually full name

        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Update user details if necessary (e.g., name, picture URL)
            user.setUsername(username); // Keep username updated from Google
            userRepository.save(user); // Save updated user (e.g., if username changed)

            grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole()));
            System.out.println("DEBUG: Existing user logged in via Google: " + user.getUsername() + ", Role: " + user.getRole());

        } else {
            // New user, register them with a default role
            user = new User(); // Initialize new User object
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(""); // OAuth2 users don't need a password for internal login
            user.setRole("ROLE_USER"); // Default role for new Google sign-ups
            user.setCreatedAt(LocalDateTime.now()); // Set creation timestamp for new users

            try {
                userRepository.save(user); // Save the new user to the database
                System.out.println("DEBUG: New Google user saved to DB: " + user.getUsername() + " with email: " + user.getEmail());
            } catch (Exception e) {
                System.err.println("ERROR: Failed to save new Google user to DB: " + e.getMessage());
                // Depending on your error handling, you might re-throw or handle gracefully
                throw new OAuth2AuthenticationException("Failed to save new Google user: " + e.getMessage());
            }

            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            System.out.println("DEBUG: New user registered via Google: " + user.getUsername() + ", Role: ROLE_USER");
        }

        // --- FINAL CRUCIAL PART: Construct DefaultOAuth2User with ONLY your custom roles ---
        // We ensure that the authorities passed are ONLY our custom ones.
        // We still need to provide the original attributes and the nameAttributeKey.
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        if (userNameAttributeName == null) {
            userNameAttributeName = "email"; // Fallback if not explicitly configured by provider
        }

        System.out.println("DEBUG: Authorities being passed to DefaultOAuth2User: " + grantedAuthorities);
        System.out.println("DEBUG: Principal name attribute: " + userNameAttributeName + ", value: " + oauth2User.getAttribute(userNameAttributeName));


        return new DefaultOAuth2User(
                grantedAuthorities, // Pass ONLY your custom roles
                oauth2User.getAttributes(), // Keep original attributes for user data retrieval
                userNameAttributeName // Specify the attribute key for the principal's name
        );
    }
}
