package com.dav.backend.features.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final StudentDetailsServiceImpl studentDetailsService;
    private final EmployeeDetailsServiceImpl employeeDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   StudentDetailsServiceImpl studentDetailsService,
                                   EmployeeDetailsServiceImpl employeeDetailsService) {
        this.jwtUtil = jwtUtil;
        this.studentDetailsService = studentDetailsService;
        this.employeeDetailsService = employeeDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;
        String role = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(jwt);
                role = jwtUtil.getRoleFromToken(jwt);
            } catch (Exception e) {
                logger.warn("Invalid JWT token: " + e.getMessage());
            }
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;

            try {
                if ("ROLE_STUDENT".equals(role)) {
                    userDetails = studentDetailsService.loadUserByUsername(username);
                } else if ("ROLE_EMPLOYEE".equals(role)) {
                    userDetails = employeeDetailsService.loadUserByUsername(username);
                } else {
                    // Fallback if role missing: try both
                    try {
                        userDetails = studentDetailsService.loadUserByUsername(username);
                    } catch (UsernameNotFoundException ex) {
                        userDetails = employeeDetailsService.loadUserByUsername(username);
                    }
                }
            } catch (UsernameNotFoundException ex) {
                logger.warn("User not found for username: " + username);
            }

            if (userDetails != null && jwtUtil.validateToken(jwt, userDetails)) {
                List<GrantedAuthority> authorities = new ArrayList<>();

                if (role != null) {
                    authorities.add(new SimpleGrantedAuthority(role));
                }
                logger.info("Granted Authorities: " + authorities);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}