package com.dav.backend.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount;

            // Prefer env variable (Docker/Prod)
            String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

            if (credentialsPath != null && !credentialsPath.isEmpty()) {
                serviceAccount = new FileInputStream(credentialsPath);
                System.out.println("Loaded Firebase credentials from env path: " + credentialsPath);
            } else {
                // Fallback to classpath (local dev)
                serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();
                System.out.println("Loaded Firebase credentials from classpath");
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId("davbanaso-48780")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
