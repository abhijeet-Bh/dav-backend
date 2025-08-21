package com.dav.backend.features.attendance;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceService {

    private static final String COLLECTION_NAME = "attendance";

    public String saveAttendance(Attendance attendance) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> future = db.collection(COLLECTION_NAME).add(attendance);
        return future.get().getId();
    }

    public Attendance getAttendanceByResultId(String resultId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .whereEqualTo("resultId", resultId)
                .get();
        List<QueryDocumentSnapshot> docs = future.get().getDocuments();
        if (!docs.isEmpty()) {
            return docs.get(0).toObject(Attendance.class);
        }
        return null;
    }

    // Update/delete methods can be added if needed
}

