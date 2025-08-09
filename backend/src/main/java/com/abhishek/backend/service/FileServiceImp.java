package com.abhishek.backend.service;

import com.abhishek.backend.model.FileDocument;
import com.abhishek.backend.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FileServiceImp implements FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private FileRepository fileRepository;

    @Override
    public String uploadFile(MultipartFile file, int downloadLimit, int hoursToExpire) throws IOException, IOException {
        String originalFilename = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFilename;

        Path path = Paths.get(uploadDir, storedFileName);
        Files.write(path, file.getBytes());

        String pin = generatePin();
        LocalDateTime now = LocalDateTime.now();

        FileDocument document = FileDocument.builder()
                .originalFileName(originalFilename)
                .storedFileName(storedFileName)
                .pin(pin)
                .filePath(path.toString())
                .uploadTime(now)
                .expireTime(now.plusHours(hoursToExpire))
                .downloadLimit(downloadLimit)
                .downloadsLeft(downloadLimit)
                .build();

        fileRepository.save(document);

        return pin;
    }

    @Override
    public FileDocument getFileByPin(String pin) {
        return fileRepository.findByPin(pin).orElse(null);
    }

    private String generatePin() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}