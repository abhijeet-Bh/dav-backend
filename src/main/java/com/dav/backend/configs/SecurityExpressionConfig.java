package com.dav.backend.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class SecurityExpressionConfig {

    @Bean
    RoleHierarchy roleHierarchy() {
        // ADMIN inherits EMPLOYEE and STUDENT
        return RoleHierarchyImpl.fromHierarchy("""
            ROLE_ADMIN > ROLE_EMPLOYEE
            ROLE_ADMIN > ROLE_STUDENT
        """);
    }

    @Bean
    MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        var handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }
}