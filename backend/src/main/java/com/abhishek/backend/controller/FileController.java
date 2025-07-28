package com.abhishek.backend.controller;

import com.abhishek.backend.model.FileDocument;
import com.abhishek.backend.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    private String uploadDir;

    private final Random random = new Random();

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("Empty file");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String storedFileName = UUID.randomUUID().randomUUID() + "_" + originalFileName;

        File uploadPath = new File(uploadDir);
        if(!uploadPath.exists()){
            uploadPath.mkdirs();
        }

        File destination = new File(uploadDir + File.separator + storedFileName);
        file.transferTo(destination);

        String pin = String.format("%06d", random.nextInt(999999));

        FileDocument document = FileDocument.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .pin(pin)
                .uploadTime(LocalDateTime.now())
                .build();

        fileRepository.save(document);

        return ResponseEntity.ok("File uploaded. PIN: " + pin);
    }
}
