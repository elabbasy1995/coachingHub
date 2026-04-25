package com.elabbasy.coatchinghub.security;

import com.elabbasy.coatchinghub.constant.Constants;
import com.elabbasy.coatchinghub.model.entity.User;
import com.elabbasy.coatchinghub.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException ex) {
                // Token expired → 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token expired\"}");
                return; // <--- important
            } catch (JwtException ex) {
                // Other JWT errors → 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid token\"}");
                return; // <--- important
            }
            catch (Exception e) {
                logger.error("Cannot extract JWT: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Optional<User> userOptional = userRepository.findDetailedByEmail(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                request.setAttribute(Constants.USER_ID_ATTRIBUTE, user.getId());
                if (Objects.nonNull(user.getAdmin())) {
                    request.setAttribute(Constants.ADMIN_ID_ATTRIBUTE, user.getAdmin().getId());
                }
                if (Objects.nonNull(user.getCoachee())) {
                    request.setAttribute(Constants.COACHEE_ID_ATTRIBUTE, user.getCoachee().getId());
                }
                if (Objects.nonNull(user.getCoach())) {
                    request.setAttribute(Constants.COACH_ID_ATTRIBUTE, user.getCoach().getId());
                }
            }

            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
