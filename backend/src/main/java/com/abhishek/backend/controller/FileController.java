package com.abhishek.backend.controller;

import com.abhishek.backend.model.FileDocument;
import com.abhishek.backend.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileRepository fileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final Random random = new Random();

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String storedFileName = UUID.randomUUID() + "_" + originalFilename;

        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        File destination = new File(uploadDir + File.separator + storedFileName);
        file.transferTo(destination);

        String pin = String.format("%06d", random.nextInt(999999));

        FileDocument document = FileDocument.builder()
                .originalFileName(originalFilename)
                .storedFileName(storedFileName)
                .pin(pin)
                .uploadTime(LocalDateTime.now())
                .build();

        fileRepository.save(document);

        return ResponseEntity.ok("File uploaded. PIN: " + pin);
    }
}
