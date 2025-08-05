package com.dav.backend.features.student;

import com.dav.backend.utils.FailureResponse;
import com.dav.backend.utils.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        try {
            Student savedStudent = studentService.addStudent(student);
            SuccessResponse<Student> response = new SuccessResponse<>(
                    savedStudent,
                    "Student created successfully!"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            FailureResponse error = new FailureResponse("Failed to create student: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{admissionNo}")
    public ResponseEntity<?> updateStudent(@PathVariable String admissionNo, @RequestBody Student student) {
        try {
            Student savedStudent = studentService.updateStudentByAdmissionNo(admissionNo, student);
            SuccessResponse<Student> response = new SuccessResponse<>(
                    savedStudent,
                    "Student updated successfully!"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            FailureResponse error = new FailureResponse("Failed to update student: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{admissionNo}")
    public ResponseEntity<?> deleteStudent(@PathVariable String admissionNo) {
        try {
            studentService.deleteStudentByAdmissionNo(admissionNo);
            SuccessResponse<String> response = new SuccessResponse<>(
                    admissionNo,
                    "Student deleted successfully!"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            FailureResponse error = new FailureResponse("Failed to delete student: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            return ResponseEntity.ok(
                    new SuccessResponse<>(students, "Students fetched successfully!")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new FailureResponse("Failed to fetch students: " + e.getMessage())
            );
        }
    }
}
