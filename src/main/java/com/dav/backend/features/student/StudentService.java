package com.dav.backend.features.student;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class StudentService {

    private static final String COLLECTION_NAME = "students";

    public Student addStudent(Student student) throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        if (isAdmissionNoExists(student.getAdmissionNo())) {
            throw new RuntimeException("Admission number already exists");
        }

        DocumentReference docRef = db.collection(COLLECTION_NAME).document();
        student.setId(docRef.getId());
        docRef.set(student).get();
        return student;
    }

    public List<Student> getAllStudents() throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Student> students = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            Student student = doc.toObject(Student.class);
            student.setId(doc.getId());
            students.add(student);
        }
        return students;
    }

    // Update student by admissionNo
    public Student updateStudentByAdmissionNo(String admissionNo, Student updatedStudent) throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        DocumentSnapshot existing = getStudentByAdmissionNo(admissionNo);
        if (existing == null) {
            throw new RuntimeException("Student not found with admissionNo: " + admissionNo);
        }

        String docId = existing.getId();
        updatedStudent.setId(docId);
        db.collection(COLLECTION_NAME).document(docId).set(updatedStudent).get();

        return updatedStudent;
    }

    // Delete student by admissionNo
    public void deleteStudentByAdmissionNo(String admissionNo) throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        DocumentSnapshot existing = getStudentByAdmissionNo(admissionNo);
        if (existing == null) {
            throw new RuntimeException("Student not found with admissionNo: " + admissionNo);
        }

        db.collection(COLLECTION_NAME).document(existing.getId()).delete().get();
    }




    // Helper: check if admissionNo exists
    private boolean isAdmissionNoExists(String admissionNo) throws ExecutionException, InterruptedException {
        return getStudentByAdmissionNo(admissionNo) != null;
    }

    // Helper: get student document by admissionNo
    private DocumentSnapshot getStudentByAdmissionNo(String admissionNo) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("admissionNo", admissionNo)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        return documents.isEmpty() ? null : documents.get(0);
    }

}
