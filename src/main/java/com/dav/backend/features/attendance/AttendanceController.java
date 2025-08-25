package com.dav.backend.features.attendance;

import com.dav.backend.utils.FailureResponse;
import com.dav.backend.utils.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/students/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<?> createAttendance(@RequestBody Attendance attendance) {
        try {
            String id = attendanceService.saveAttendance(attendance);
            HashMap<String, String> map = new HashMap<>();
            map.put("attendanceId", id);
            return ResponseEntity.ok(new SuccessResponse<>(map,"Attendance saved successfully!!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new FailureResponse("Error saving attendance: " + e.getMessage()));
        }
    }

    @GetMapping("/result/{resultId}")
    public ResponseEntity<?> getAttendanceByResultId(@PathVariable String resultId) {
        try {
            Attendance attendance = attendanceService.getAttendanceByResultId(resultId);
            if (attendance == null) return ResponseEntity.status(404).body(new FailureResponse("Attendance not found"));
            return ResponseEntity.ok(new SuccessResponse<>(attendance, "Attendance fetched successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new FailureResponse("Error fetching attendance: " + e.getMessage()));
        }
    }
}

