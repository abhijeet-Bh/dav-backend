package com.dav.backend.features.employee;

import com.dav.backend.utils.FailureResponse;
import com.dav.backend.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // CREATE
    @PostMapping
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

