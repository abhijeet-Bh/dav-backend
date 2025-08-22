package com.dav.backend.features.employee;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private static final String COLLECTION_NAME = "employees";

    @Autowired
    PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee findByEmployeeId(String employeeId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("employeeId", employeeId)
                    .limit(1)
                    .get();
            QuerySnapshot querySnapshot = future.get();
            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                return doc.toObject(Employee.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // CREATE
    public Employee addEmployee(Employee employee) throws Exception {
        if (employeeRepository.getDocumentByEmployeeId(employee.getEmployeeId()) != null) {
            throw new Exception("Employee ID already exists!");
        }
        // Encode password before saving
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("employees").document(employee.getEmployeeId());
        employee.setId(employee.getEmployeeId());
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

