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

    public List<StudentResults> getResultsByAdmission(String admissionNo) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("studentId", admissionNo)
                .get();

        List<QueryDocumentSnapshot> docs = future.get().getDocuments();

        if (docs.isEmpty()) {
            throw new RuntimeException("Results with admission No.: " + admissionNo + " not found!");
        }

        List<StudentResults> resultsList = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            resultsList.add(doc.toObject(StudentResults.class));
        }

        return resultsList;
    }


    public List<StudentResultsDTO> getResultsDTOByAdmission(String admissionNo) throws Exception {
        List<StudentResults> resultsList = getResultsByAdmission(admissionNo);
        Student student = studentService.getStudentByAdmissionNumber(admissionNo);

        List<StudentResultsDTO> dtoList = new ArrayList<>();

        for (StudentResults results : resultsList) {
            Attendance attendance = attendanceService.getAttendanceByResultId(results.getId());

            StudentResultsDTO dto = StudentResultsDTO.builder()
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
                    .downloadLink("/api/v1/students/results/download-pdf/" + results.getId())
                    .build();

            dtoList.add(dto);
        }

        return dtoList;
    }

    public StudentResultsDTO getSingleResultDTOById(String resultId) throws Exception{
        StudentResults result = getResultById(resultId);
        Student student = studentService.getStudentByAdmissionNumber(result.getStudentId());
        Attendance attendance = attendanceService.getAttendanceByResultId(result.getId());
        return StudentResultsDTO.builder()
                .id(result.getId())
                .session(result.getSession())
                .examType(result.getExamType())
                .student(student)
                .rollNo(result.getRollNo())
                .marks(result.getMarks())
                .result(result.getResult())
                .percentage(HelperFunctions.getPercentage(result.getMarks()))
                .attendance(attendance)
                .remark(result.getRemark())
                .place(result.getPlace())
                .date(result.getDate())
                .build();
    }

}

