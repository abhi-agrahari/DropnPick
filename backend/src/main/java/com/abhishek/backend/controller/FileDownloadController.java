package com.abhishek.backend.controller;

import com.abhishek.backend.model.FileDocument;
import com.abhishek.backend.repository.FileRepository;
import com.abhishek.backend.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/files")
public class FileDownloadController {

    @Autowired
    private FileRepository fileRepository;

    @GetMapping("/download/{pin}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String pin) throws IOException {
        FileDocument fileDocument = fileRepository.findByPin(pin)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (fileDocument.getDownloadsLeft() <= 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }

        Path filePath = Paths.get(fileDocument.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File not found on disk");
        }

        fileDocument.setDownloadsLeft(fileDocument.getDownloadsLeft() - 1);
        fileRepository.save(fileDocument);

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileDocument.getOriginalFileName() + "\"")
                .body(resource);
    }
}