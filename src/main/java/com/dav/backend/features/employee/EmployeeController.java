package com.dav.backend.features.employee;

import com.dav.backend.exceptions.CustomException;
import com.dav.backend.exceptions.ErrorCode;
import com.dav.backend.features.auth.JwtResponse;
import com.dav.backend.features.auth.JwtUtil;
import com.dav.backend.features.auth.LoginRequest;
import com.dav.backend.utils.FailureResponse;
import com.dav.backend.utils.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Employee employee = employeeService.findByEmployeeId(request.getEmployeeId());
        if(request.getEmployeeId() == null || request.getPassword().isEmpty())
            throw new RuntimeException("Employee Id cannot be empty!");
        if(employee == null)
            throw new CustomException(ErrorCode.EMPLOYEE_NOT_FOUND,"Employee with id: " + request.getEmployeeId() + " Not found :(");

        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new BadCredentialsException("Invalid Password!");
        }

        String token = jwtUtil.generateToken(employee.getEmployeeId(), employee.getRole());
        return ResponseEntity.ok(new JwtResponse(token, employee.getRole()));
    }

    // CREATE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addEmployee(@RequestBody Employee employee) {
        try {
            Employee savedEmployee = employeeService.addEmployee(employee);
            return ResponseEntity.ok(new SuccessResponse<>(savedEmployee, "Employee created successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FailureResponse(e.getMessage()));
        }
    }

    // READ ALL
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            return ResponseEntity.ok(new SuccessResponse<>(employees, "Employees fetched successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FailureResponse(e.getMessage()));
        }
    }

    // READ BY EMPLOYEE ID
    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<?> getEmployee(@PathVariable String employeeId) {
        try {
            Employee employee = employeeService.getEmployeeByEmployeeId(employeeId);
            return ResponseEntity.ok(new SuccessResponse<>(employee, "Employee fetched successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FailureResponse(e.getMessage()));
        }
    }

    // UPDATE
    @PutMapping("/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEmployee(@PathVariable String employeeId, @RequestBody Employee employee) {
        try {
            Employee updated = employeeService.updateEmployeeByEmployeeId(employeeId, employee);
            return ResponseEntity.ok(new SuccessResponse<>(updated, "Employee updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FailureResponse(e.getMessage()));
        }
    }

    // DELETE
    @DeleteMapping("/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable String employeeId) {
        try {
            employeeService.deleteEmployeeByEmployeeId(employeeId);
            return ResponseEntity.ok(new SuccessResponse<>(null, "Employee deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FailureResponse(e.getMessage()));
        }
    }
}

