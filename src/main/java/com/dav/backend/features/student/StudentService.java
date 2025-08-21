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

        DocumentReference docRef = db.collection(COLLECTION_NAME).document(student.getAdmissionNo());
        student.setId(student.getAdmissionNo());
        docRef.set(student).get();
        return student;
    }

    public Student getStudentByAdmissionNumber(String admissionNum) throws ExecutionException, InterruptedException {
        DocumentSnapshot docSnapshot = getStudentByAdmissionNo(admissionNum);
        if (docSnapshot == null || !docSnapshot.exists()) {
            throw new RuntimeException("Student not found with admissionNo: " + admissionNum);
        }
        return docSnapshot.toObject(Student.class);
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

    // Get Students on pagination based response
    public PaginatedResponse<Student> getStudentsPaginated(String className, String section, int size, String pageToken, int page) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference colRef = db.collection(COLLECTION_NAME);

        Query query = colRef;

        if (className != null && !className.isEmpty()) {
            query = query.whereEqualTo("className", className);
        }
        if (section != null && !section.isEmpty()) {
            query = query.whereEqualTo("section", section);
        }

        query = query.orderBy("admissionNo").limit(size);

        if (pageToken != null && !pageToken.isEmpty()) {
            DocumentSnapshot lastDoc = colRef.document(pageToken).get().get();
            if (lastDoc.exists()) {
                query = query.startAfter(lastDoc);
            }
        }

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Student> students = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            Student student = doc.toObject(Student.class);
            student.setId(doc.getId());
            students.add(student);
        }

        // Calculate totalCount via a count query with same filters
        ApiFuture<QuerySnapshot> countFuture;
        if ((className != null && !className.isEmpty()) && (section != null && !section.isEmpty())) {
            countFuture = colRef.whereEqualTo("className", className)
                    .whereEqualTo("section", section)
                    .get();
        } else if (className != null && !className.isEmpty()) {
            countFuture = colRef.whereEqualTo("className", className).get();
        } else if (section != null && !section.isEmpty()) {
            countFuture = colRef.whereEqualTo("section", section).get();
        } else {
            countFuture = colRef.get();
        }

        int totalCount = countFuture.get().size();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        String nextPageToken = null;
        if (documents.size() == size) {
            nextPageToken = documents.get(documents.size() - 1).getId();
        }

        return new PaginatedResponse<>(students, nextPageToken, page, totalPages);
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
