package com.dav.backend.features.results;

import com.dav.backend.features.attendance.AttendanceService;
import com.dav.backend.features.student.Student;
import com.dav.backend.features.student.StudentService;
import com.dav.backend.utils.FailureResponse;
import com.dav.backend.utils.ResultsUtil;
import com.dav.backend.utils.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/api/v1/students/results")
public class StudentResultsController {

    @Autowired
    private StudentResultsService resultsService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private ResultsUtil resultsUtil;

    @PostMapping
    public ResponseEntity<?> createResult(@RequestBody StudentResults result) {
        try {
            Student student = studentService.getStudentByAdmissionNumber(result.getStudentId());
            String id = resultsService.saveResult(result);
            HashMap<String, String> map = new HashMap<>();
            map.put("resultId", id);

            return ResponseEntity.ok(new SuccessResponse<>(
                    map,
                    "Result created successfully!"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new FailureResponse("Error creating result: " + e.getMessage()));
        }
    }

    @GetMapping("/{resultId}")
    public ResponseEntity<?> getResultById(@PathVariable String resultId) {
        try {
            StudentResults result = resultsService.getResultById(resultId);
            if (result == null) return ResponseEntity.status(404).body("Result not found");
            return ResponseEntity.ok(new SuccessResponse<>(
                    result,
                    "Result fetched successfully!"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new FailureResponse("Error fetching result: " + e.getMessage())
            );
        }
    }

    @GetMapping("/student/{admissionNo}")
    public ResponseEntity<?> getResultsByAdmissionAndSession(
            @PathVariable String admissionNo) {
        try {
            List<StudentResultsDTO> studentResultsDTO = resultsService.getResultsDTOByAdmission(admissionNo);
            return ResponseEntity.ok().body(
                    new SuccessResponse<>(studentResultsDTO, "Results fetched successfully!")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new FailureResponse("Error fetching results: " + e.getMessage()));
        }
    }

    // Download Results
    @GetMapping("/download-pdf/{id}")
    public ResponseEntity<?> downloadResultPdf(@PathVariable String id) throws Exception {
        try {
            StudentResultsDTO studentResultsDTO = resultsService.getSingleResultDTOById(id);
            byte[] pdfBytes = resultsUtil.fillResultPdf(studentResultsDTO);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Result-" +
                            studentResultsDTO.getStudent().getStudentName().split(" ")[0] + "-" +
                            studentResultsDTO.getStudent().getAdmissionNo()+ "-" +
                            studentResultsDTO.getExamType() + "-" +
                            studentResultsDTO.getSession() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        }catch (Exception e){
            return ResponseEntity.status(500).body(
                    new FailureResponse("Failed to download result: " + e.getMessage())
            );
        }
    }
}

