package com.tasky.api.filters;

import com.tasky.api.services.user.UserDetailsService;
import com.tasky.api.utilities.JwtUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom authentication filter that processes JWT tokens and sets the authenticated user in the SecurityContextHolder.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtility jwtUtility;
    private final UserDetailsService userDetailsService;

    /**
     * Constructs a JwtAuthenticationFilter with the specified JwtUtility and UserDetailsService.
     *
     * @param jwtUtility The JwtUtility implementation.
     * @param userDetailsService The UserDetailsService implementation.
     */
    public JwtAuthenticationFilter(JwtUtility jwtUtility, UserDetailsService userDetailsService) {
        this.jwtUtility = jwtUtility;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Performs JWT token authentication and sets the authenticated user in the SecurityContextHolder.
     *
     * @param request The incoming HttpServletRequest.
     * @param response The HttpServletResponse.
     * @param filterChain The FilterChain for processing the request.
     * @throws ServletException If a servlet exception occurs.
     * @throws IOException If an I/O exception occurs.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String subject = jwtUtility.getSubject(jwt);

        if(subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

            if(jwtUtility.isTokenValid(jwt, userDetails.getUsername())) {

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request,response);

        }
    }
}
