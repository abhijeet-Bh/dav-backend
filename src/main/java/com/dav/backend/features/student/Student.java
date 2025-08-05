package com.dav.backend.features.student;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;
import com.google.cloud.firestore.annotation.DocumentId;

@Data
public class Student {

    @DocumentId
    private String id; // Firestore uses document ID, not serial numbers

    private String admissionNo;
    private String studentName;
    private String mobileNo;
    private String className;
    private String section;
    private String fatherName;
    private String motherName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;
    private String address;
}
