package com.dav.backend.features.results;

import com.dav.backend.features.attendance.AttendanceService;
import com.dav.backend.features.student.Student;
import com.dav.backend.features.student.StudentService;
import com.dav.backend.utils.FailureResponse;
import com.dav.backend.utils.ResultsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
            return ResponseEntity.ok("Result created with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating result: " + e.getMessage());
        }
    }

    @GetMapping("/{resultId}")
    public ResponseEntity<?> getResultById(@PathVariable String resultId) {
        try {
            StudentResults result = resultsService.getResultById(resultId);
            if (result == null) return ResponseEntity.status(404).body("Result not found");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching result: " + e.getMessage());
        }
    }

    @GetMapping("/student")
    public ResponseEntity<?> getResultsByAdmissionAndSession(
            @RequestParam String admissionNo,
            @RequestParam String session) {
        try {
            StudentResultsDTO studentResultsDTO = resultsService.getResultByAdmissionNumAndSession(admissionNo, session);
            studentResultsDTO.setDownloadLink("/api/v1/students/results/download-pdf?admissionNo=" + admissionNo + "&session=" + session);
            return ResponseEntity.ok().body(studentResultsDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching results: " + e.getMessage());
        }
    }

    // Download Results
    @GetMapping("/download-pdf")
    public ResponseEntity<?> downloadResultPdf(@RequestParam String admissionNo,
                                               @RequestParam String session) throws Exception {
        try {
            StudentResultsDTO studentResultsDTO = resultsService.getResultByAdmissionNumAndSession(admissionNo, session);
            byte[] pdfBytes = resultsUtil.fillResultPdf(studentResultsDTO);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=result_" + admissionNo + session + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        }catch (Exception e){
            return ResponseEntity.status(500).body(
                    new FailureResponse("Failed to download result: " + e.getMessage())
            );
        }
    }
}

