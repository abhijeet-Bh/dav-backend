package com.dav.backend.configs;

import com.dav.backend.exceptions.ErrorCode;
import com.dav.backend.features.auth.EmployeeDetailsServiceImpl;
import com.dav.backend.features.auth.JwtAuthenticationFilter;
import com.dav.backend.features.auth.JwtUtil;
import com.dav.backend.features.auth.StudentDetailsServiceImpl;
import com.dav.backend.utils.FailureResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {
    private final JwtUtil jwtUtil;
    private final StudentDetailsServiceImpl studentDetailsService;
    private final EmployeeDetailsServiceImpl employeeDetailsService;

    public SpringSecurityConfig(JwtUtil jwtUtil,
                                StudentDetailsServiceImpl studentDetailsService,
                                EmployeeDetailsServiceImpl employeeDetailsService) {
        this.jwtUtil = jwtUtil;
        this.studentDetailsService = studentDetailsService;
        this.employeeDetailsService = employeeDetailsService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, studentDetailsService, employeeDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/api/v1/students/login",
                                "/api/v1/employees/login"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(customAccessDeniedHandler())
                        .authenticationEntryPoint(customAuthEntryPoint())
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");

            FailureResponse body = new FailureResponse(ErrorCode.ACCESS_DENIED.getDefaultMessage());

            new ObjectMapper().writeValue(response.getWriter(), body);
        };
    }

    @Bean
    public AuthenticationEntryPoint customAuthEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");

            String message = ErrorCode.UNAUTHORIZED.getDefaultMessage();

            if (request.getAttribute("jwt_expired") != null) {
                message = ErrorCode.JWT_EXPIRED.getDefaultMessage();
            }

            FailureResponse body = new FailureResponse(message);

            new ObjectMapper().writeValue(response.getWriter(), body);
        };
    }
}
