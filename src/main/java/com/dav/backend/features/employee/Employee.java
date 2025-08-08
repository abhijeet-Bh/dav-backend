package com.dav.backend.features.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @DocumentId
    private String id; // Firestore document ID

    private String employeeId; // Unique Employee ID
    private String name;
    private String mobileNo;
    private String designation;
    private String department;
    private String email;
    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date joiningDate;
}
