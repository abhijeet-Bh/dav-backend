package com.dav.backend.features.student;

import com.dav.backend.utils.FailureResponse;
import com.dav.backend.utils.StudentExcelImporter;
import com.dav.backend.utils.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> getAllStudents(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String pageToken
    ) {
        try {
            PaginatedResponse<Student> response = studentService.getStudentsPaginated(className, section, size, pageToken, page);
            return ResponseEntity.ok(new SuccessResponse<>(response, "Students fetched successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FailureResponse("Failed to fetch students: " + e.getMessage()));
        }
    }





    // Bulk Import for students
    @PostMapping("/import")
    public ResponseEntity<?> importStudents(@RequestParam("file") MultipartFile file) {
        List<Integer> errorRows = new ArrayList<>();
        List<Student> addedStudents = new ArrayList<>();
        try {
            List<Student> students = StudentExcelImporter.importStudents(file.getInputStream(), errorRows);
            for (Student s : students) {
                Student res = studentService.addStudent(s);
            addedStudents.add(res);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("total", students.size());
            result.put("errorRows", errorRows);
            result.put("students", addedStudents);

            // get error rows
            if(!errorRows.isEmpty()) {
                System.out.println("Rows with errors:");
                for(Integer row : errorRows) {
                    System.out.println("Row: " + row);
                }
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed: " + e.getMessage());
        }
    }

}
