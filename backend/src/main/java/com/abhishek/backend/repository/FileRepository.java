package com.abhishek.backend.repository;

import com.abhishek.backend.model.FileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FileRepository extends MongoRepository<FileDocument, String> {
    Optional<FileDocument> findPin(String pin);
}
