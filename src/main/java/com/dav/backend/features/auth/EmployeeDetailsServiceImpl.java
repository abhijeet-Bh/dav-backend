package com.dav.backend.features.auth;

import com.dav.backend.features.employee.Employee;
import com.dav.backend.features.employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmployeeDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private EmployeeService employeeService;

    @Override
    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {
        Employee employee = employeeService.findByEmployeeId(employeeId);
        if (employee == null) {
            throw new UsernameNotFoundException("Employee not found with employeeId: " + employeeId);
        }
        return employee;
    }
}

