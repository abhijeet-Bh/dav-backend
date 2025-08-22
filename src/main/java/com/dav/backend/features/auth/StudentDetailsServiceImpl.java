package com.dav.backend.features.auth;

import com.dav.backend.features.student.Student;
import com.dav.backend.features.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StudentDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private StudentService studentService;

    @Override
    public UserDetails loadUserByUsername(String admissionNo) throws UsernameNotFoundException {
        Student student = studentService.findByAdmissionNo(admissionNo);
        if (student == null) {
            throw new UsernameNotFoundException("Student not found with admissionNo: " + admissionNo);
        }
        return student;
    }
}

