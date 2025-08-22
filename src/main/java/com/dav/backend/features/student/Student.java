package com.dav.backend.features.student;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Student implements UserDetails {
    @DocumentId
    private String id;
    private String admissionNo;
    private String studentName;
    private String mobileNo;
    private String className;
    private String section;
    private String fatherName;
    private String motherName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;
    private String address;

    // Auth fields
    private String password;
    private String role = "ROLE_STUDENT";
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    @com.google.cloud.firestore.annotation.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Override
    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @com.google.cloud.firestore.annotation.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Override
    public String getUsername() {
        return this.admissionNo;
    }

    @com.google.cloud.firestore.annotation.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Override public boolean isAccountNonExpired() { return accountNonExpired; }

    @com.google.cloud.firestore.annotation.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Override public boolean isAccountNonLocked() { return accountNonLocked; }

    @com.google.cloud.firestore.annotation.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Override public boolean isCredentialsNonExpired() { return credentialsNonExpired; }

    @com.google.cloud.firestore.annotation.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Override public boolean isEnabled() { return enabled; }
}
