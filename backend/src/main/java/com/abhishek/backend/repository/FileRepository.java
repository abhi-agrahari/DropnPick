package com.abhishek.backend.repository;

import com.abhishek.backend.model.FileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<FileDocument, String> {
    Optional<FileDocument> findByPin(String pin);
}
