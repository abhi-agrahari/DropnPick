package com.abhishek.backend.service;

import com.abhishek.backend.model.FileDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface FileService {
    String uploadFile(MultipartFile file, int downloadLimit, int timeLimitHours) throws IOException;
    FileDocument getFileByPin(String pin);
}