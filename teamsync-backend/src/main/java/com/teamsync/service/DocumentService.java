package com.teamsync.service;

import com.teamsync.entity.Document;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface DocumentService {
    Document findById(Long id);
    List<Document> findByProjectId(Long projectId);
    List<Document> findAll();
    Document upload(Long projectId, Long uploaderId, String category, MultipartFile file);
    void deleteById(Long id);
    void incrementDownloadCount(Long id);
}
