package com.dav.backend.features.employee;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private static final String COLLECTION_NAME = "employees";

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // CREATE
    public Employee addEmployee(Employee employee) throws Exception {
        // Check if employeeId already exists
        if (employeeRepository.getDocumentByEmployeeId(employee.getEmployeeId()) != null) {
            throw new Exception("Employee ID already exists!");
        }
        DocumentReference docRef = employeeRepository.getCollection().document();
        employee.setId(docRef.getId());
        docRef.set(employee).get();
        return employee;
    }

    // READ ALL
    public List<Employee> getAllEmployees() throws ExecutionException, InterruptedException {
        List<Employee> employees = new ArrayList<>();
        var docs = employeeRepository.getCollection().get().get().getDocuments();
        for (QueryDocumentSnapshot doc : docs) {
            employees.add(doc.toObject(Employee.class));
        }
        return employees;
    }

    // READ BY EMPLOYEE ID
    public Employee getEmployeeByEmployeeId(String employeeId) throws Exception {
        DocumentReference docRef = employeeRepository.getDocumentByEmployeeId(employeeId);
        if (docRef == null) throw new Exception("Employee not found");
        return docRef.get().get().toObject(Employee.class);
    }

    // UPDATE BY EMPLOYEE ID
    public Employee updateEmployeeByEmployeeId(String employeeId, Employee employee) throws Exception {
        DocumentReference docRef = employeeRepository.getDocumentByEmployeeId(employeeId);
        if (docRef == null) throw new Exception("Employee not found");
        employee.setId(docRef.getId());
        docRef.set(employee).get();
        return employee;
    }

    // DELETE BY EMPLOYEE ID
    public void deleteEmployeeByEmployeeId(String employeeId) throws Exception {
        DocumentReference docRef = employeeRepository.getDocumentByEmployeeId(employeeId);
        if (docRef == null) throw new Exception("Employee not found");
        docRef.delete().get();
    }
}

