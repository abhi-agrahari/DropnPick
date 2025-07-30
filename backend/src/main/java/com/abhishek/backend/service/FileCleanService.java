package com.abhishek.backend.service;

import com.abhishek.backend.model.FileDocument;
import com.abhishek.backend.repository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileCleanService {

    private final FileRepository fileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileCleanService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Scheduled(fixedRate = 24*3600000)
    public void cleanUp() {
        List<FileDocument> expiredFiles = fileRepository.findAll()
                .stream()
                .filter(file -> file.getExpireTime().isBefore(LocalDateTime.now()))
                .toList();

        for(FileDocument expiredFile : expiredFiles) {
            try {
                Path path = Paths.get(expiredFile.getFilePath());
                Files.deleteIfExists(path);
                fileRepository.delete(expiredFile);
            }
            catch (Exception e) {
                System.err.println("Failed to delete file: " + expiredFile.getOriginalFileName());
            }
        }
    }
}
