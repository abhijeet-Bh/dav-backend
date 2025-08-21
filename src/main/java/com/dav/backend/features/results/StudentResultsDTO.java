package com.dav.backend.features.results;

import com.dav.backend.features.attendance.Attendance;
import com.dav.backend.features.student.Student;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentResultsDTO {
    private String id;
    private Student student;
    private String rollNo;
    private String session;
    private String examType;
    private List<SubjectResult> marks;
    private double percentage;
    private String result;
    private Attendance attendance;
    private String remark;
    private String place;
    private String date;
    private String downloadLink;
}
