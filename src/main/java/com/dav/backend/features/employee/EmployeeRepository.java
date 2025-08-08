package com.dav.backend.features.employee;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository
public class EmployeeRepository {

    private static final String COLLECTION_NAME = "employees";

    private Firestore getDB() {
        return FirestoreClient.getFirestore();
    }

    public DocumentReference getDocumentByEmployeeId(String employeeId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getDB()
                .collection(COLLECTION_NAME)
                .whereEqualTo("employeeId", employeeId)
                .limit(1)
                .get();
        QuerySnapshot snapshot = future.get();
        if (!snapshot.isEmpty()) {
            return snapshot.getDocuments().get(0).getReference();
        }
        return null;
    }

    public CollectionReference getCollection() {
        return getDB().collection(COLLECTION_NAME);
    }
}

