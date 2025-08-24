package com.dav.backend.features.student;

import com.dav.backend.exceptions.CustomException;
import com.dav.backend.exceptions.ErrorCode;
import com.dav.backend.features.auth.JwtUtil;
import com.dav.backend.features.auth.LoginRequest;
import com.dav.backend.utils.FailureResponse;
import com.dav.backend.utils.StudentExcelImporter;
import com.dav.backend.utils.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.getAdmissionNo() == null || request.getAdmissionNo().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RuntimeException("Admission No and password cannot be empty!");
        }
        Student findStudent = studentService.findByAdmissionNo(request.getAdmissionNo());
        if(findStudent == null)
            throw new CustomException(ErrorCode.STUDENT_NOT_FOUND, "Student with is: " + request.getAdmissionNo() + " Not found!");
        else if (!passwordEncoder.matches(request.getPassword(), findStudent.getPassword()))
            throw new BadCredentialsException("Invalid Password!");

        // Delegate authentication to AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getAdmissionNo(), request.getPassword())
        );

        Student student = (Student) authentication.getPrincipal();

        String token = jwtUtil.generateToken(student.getAdmissionNo(), student.getRole());

        HashMap<String, Object> data = new HashMap<>();
        data.put("accessToken", token);
        data.put("profile", student);
        data.put("role", student.getRole());

        return ResponseEntity.ok(new SuccessResponse<>(data, "Logged In Successfully!"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
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
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping("/paginated")
    public ResponseEntity<?> getAllStudentsPaginated(
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

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> response = studentService.getAllStudents();
            return ResponseEntity.ok(new SuccessResponse<>(response, "Students fetched successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FailureResponse("Failed to fetch students: " + e.getMessage()));
        }
    }


    // Bulk Import for students
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> importStudents(@RequestParam("file") MultipartFile file) {
        List<Integer> errorRows = new ArrayList<>();
        List<Student> addedStudents = new ArrayList<>();
        try {
            List<Student> students = StudentExcelImporter.importStudents(file.getInputStream(), errorRows);
            for (Student s : students) {
                try {
                    Student res = studentService.addStudent(s);
                    addedStudents.add(res);
                }catch (Exception e){
                    log.error(e.getMessage());
                    errorRows.add(Integer.valueOf(s.getAdmissionNo()));
                }
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
            // get error rows
            if(!errorRows.isEmpty()) {
                System.out.println("Rows with errors:");
                for(Integer row : errorRows) {
                    System.out.println("Row: " + row);
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed: " + e.getMessage());
        }
    }

}
