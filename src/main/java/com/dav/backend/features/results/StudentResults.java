package com.dav.backend.features.results;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResults {
    @DocumentId
    private String id;
    private String studentId;
    private String rollNo;
    private String session;
    private String examType;
    private List<SubjectResult> marks;
    private String result;
    private String remark;
    private String place;
    private String date;
}

