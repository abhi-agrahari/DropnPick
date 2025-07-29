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
import java.net.MalformedURLException;
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
    public ResponseEntity<Resource> downloadFile(@PathVariable String pin) throws IOException {
        System.out.println("Received download request for PIN: " + pin);

        FileDocument doc = fileService.getFileByPin(pin);
        if (doc == null) {
            System.out.println("No file found for PIN: " + pin);
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(doc.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            System.out.println("File not found on disk: " + path.toString());
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getOriginalFileName() + "\"")
                .body(resource);
    }
}