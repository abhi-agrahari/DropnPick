package com.abhishek.backend.controller;

import com.abhishek.backend.model.FileDocument;
import com.abhishek.backend.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try{
            String pin = fileService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully. Your PIN is: " + pin);
        }
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    @GetMapping("/download/{pin}")
    public ResponseEntity<?> downloadFile(@PathVariable String pin) throws IOException {
        FileDocument doc = fileService.getFileByPin(pin);

        if(doc == null) {
            System.out.println("No file found for PIN: " + pin);
            return ResponseEntity.notFound().build();
        }

        if(doc.getExpireTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity
                    .status(HttpStatus.GONE)
                    .body("File has expired and is no longer available.");
        }

        Path path = Paths.get(doc.getFilePath());

        if(!Files.exists(path)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("File does not exist on disk.");
        }

        byte[] fileBytes = Files.readAllBytes(path);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getOriginalFileName() + "\"")
                .body(fileBytes);
    }
}