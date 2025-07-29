package com.abhishek.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDocument {

    @Id
    private String id;
    private String originalFileName;
    private String storedFileName;
    private String pin;
    private String filePath;
    private LocalDateTime uploadTime;
    private LocalDateTime expireTime;
}
