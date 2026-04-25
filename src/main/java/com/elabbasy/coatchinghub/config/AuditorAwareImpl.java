package com.elabbasy.coatchinghub.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    private static final String DEFAULT = "ANOMALOUS";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return Optional.of(DEFAULT);
        }

        Object principal = auth.getPrincipal();

        // Your principal is usually CustomUserDetails in JWT setup
        if (principal instanceof UserDetails userDetails) {
            return Optional.ofNullable(userDetails.getUsername()); // or user id/email
        } else if (principal instanceof String username) {
            return Optional.of(username);
        }

        return Optional.of(DEFAULT);
    }
}
