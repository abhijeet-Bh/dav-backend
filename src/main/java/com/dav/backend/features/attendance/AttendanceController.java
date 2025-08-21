package com.dav.backend.features.attendance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<?> createAttendance(@RequestBody Attendance attendance) {
        try {
            String id = attendanceService.saveAttendance(attendance);
            return ResponseEntity.ok("Attendance saved with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving attendance: " + e.getMessage());
        }
    }

    @GetMapping("/result/{resultId}")
    public ResponseEntity<?> getAttendanceByResultId(@PathVariable String resultId) {
        try {
            Attendance attendance = attendanceService.getAttendanceByResultId(resultId);
            if (attendance == null) return ResponseEntity.status(404).body("Attendance not found");
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching attendance: " + e.getMessage());
        }
    }
}

