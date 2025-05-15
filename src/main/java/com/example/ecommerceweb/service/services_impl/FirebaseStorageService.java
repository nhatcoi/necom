package com.example.ecommerceweb.service.services_impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseStorageService {

    @Value("${firebase.storage-bucket}")
    private String storageBucket;

    @Value("${firebase.service-account-path}")
    private String serviceAccountPath;

    private static final String LOCAL_UPLOAD_DIR = "uploads";

    public String uploadImage(MultipartFile file) throws IOException {
        try {
            // Try Firebase upload first
            return uploadToFirebase(file);
        } catch (Exception e) {
            log.warn("Firebase upload failed, falling back to local storage: {}", e.getMessage());
            return uploadToLocalStorage(file);
        }
    }

    private String uploadToFirebase(MultipartFile file) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
        return blob.getMediaLink();
    }

    private String uploadToLocalStorage(MultipartFile file) throws IOException {
        // Create uploads directory if it doesn't exist
        File uploadDir = new File(LOCAL_UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Generate unique filename
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Path filePath = Paths.get(LOCAL_UPLOAD_DIR, fileName);

        // Save file
        Files.write(filePath, file.getBytes());

        // Return local URL
        return "/uploads/" + fileName;
    }
}
