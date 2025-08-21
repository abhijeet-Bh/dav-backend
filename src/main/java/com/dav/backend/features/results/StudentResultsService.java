package com.dav.backend.features.results;

import com.dav.backend.features.attendance.Attendance;
import com.dav.backend.features.attendance.AttendanceService;
import com.dav.backend.features.student.Student;
import com.dav.backend.features.student.StudentService;
import com.dav.backend.utils.HelperFunctions;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentResultsService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AttendanceService attendanceService;

    private static final String COLLECTION_NAME = "results";

    public String saveResult(StudentResults result) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> future = db.collection(COLLECTION_NAME).add(result);
        return future.get().getId();
    }

    public StudentResults getResultById(String resultId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc = db.collection(COLLECTION_NAME).document(resultId).get().get();
        return doc.exists() ? doc.toObject(StudentResults.class) : null;
    }

    public StudentResults getResultByAdmissionAndSession(String admissionNo, String session) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("studentId", admissionNo)
                .whereEqualTo("session", session)
                .limit(1)
                .get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();
        if (docs.isEmpty()) {
            throw new RuntimeException("Result with admission No.: " + admissionNo + " and session.: " + session + " Not found!");
        }
        return docs.get(0).toObject(StudentResults.class);
    }

    public StudentResultsDTO getResultByAdmissionNumAndSession(String admissionNo, String session) throws Exception{
        StudentResults results = getResultByAdmissionAndSession(admissionNo, session);
        Student student = studentService.getStudentByAdmissionNumber(admissionNo);
        Attendance attendance = attendanceService.getAttendanceByResultId(results.getId());
        return StudentResultsDTO.builder()
                        .id(results.getId())
                        .session(results.getSession())
                        .examType(results.getExamType())
                        .student(student)
                        .rollNo(results.getRollNo())
                        .marks(results.getMarks())
                        .result(results.getResult())
                        .percentage(HelperFunctions.getPercentage(results.getMarks()))
                        .attendance(attendance)
                        .remark(results.getRemark())
                        .place(results.getPlace())
                        .date(results.getDate())
                        .build();
    }


    // Additional update/delete methods can be added here
}

