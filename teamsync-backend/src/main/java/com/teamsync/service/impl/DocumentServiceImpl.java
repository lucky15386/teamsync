package com.teamsync.service.impl;

import com.teamsync.entity.Document;
import com.teamsync.mapper.DocumentMapper;
import com.teamsync.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    private DocumentMapper documentMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private String getAbsoluteUploadPath() {
        String path = uploadDir;
        if (path.startsWith("~" + System.getProperty("file.separator"))) {
            path = System.getProperty("user.home") + path.substring(1);
        } else if (path.startsWith("~")) {
            path = System.getProperty("user.home") + path.substring(1);
        }
        return path;
    }

    @Override
    public Document findById(Long id) {
        return documentMapper.findById(id);
    }

    @Override
    public List<Document> findByProjectId(Long projectId) {
        return documentMapper.findByProjectId(projectId);
    }

    @Override
    public List<Document> findAll() {
        return documentMapper.findAll();
    }

    @Override
    public Document upload(Long projectId, Long uploaderId, String category, MultipartFile file) {
        if (file.isEmpty()) {
            logger.error("文件上传失败：文件为空");
            throw new RuntimeException("上传文件不能为空");
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFilename = UUID.randomUUID().toString() + extension;

        try {
            // 使用绝对路径确保目录存在
            String resolvedPath = getAbsoluteUploadPath();
            Path uploadPath = Paths.get(resolvedPath).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            
            Path targetPath = uploadPath.resolve(storedFilename);
            
            logger.info("准备保存文件到: {}", targetPath.toString());
            
            // 使用 Java NIO 保存文件
            Files.copy(file.getInputStream(), targetPath);
            
            logger.info("文件保存成功: {}", targetPath.toString());

            // 保存文档记录
            Document doc = new Document();
            doc.setProjectId(projectId);
            doc.setName(originalFilename);
            doc.setFilePath(targetPath.toString());
            doc.setFileUrl("/uploads/" + storedFilename);
            doc.setFileSize(file.getSize());
            
            // 智能判断文件类型
            String fileType = file.getContentType();
            if (fileType == null || fileType.isEmpty() || "application/octet-stream".equals(fileType)) {
                fileType = guessFileType(originalFilename);
            }
            doc.setFileType(fileType);
            
            doc.setUploaderId(uploaderId);
            doc.setCategory(category != null ? category : "其他");
            doc.setDownloadCount(0);
            documentMapper.insert(doc);

            return documentMapper.findById(doc.getId());
            
        } catch (IOException e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) {
        Document doc = documentMapper.findById(id);
        if (doc != null) {
            try {
                Path filePath = Paths.get(doc.getFilePath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    logger.info("已删除文件: {}", doc.getFilePath());
                }
            } catch (IOException e) {
                logger.warn("删除文件失败: {}", e.getMessage());
            }
            documentMapper.deleteById(id);
        }
    }

    @Override
    public void incrementDownloadCount(Long id) {
        documentMapper.incrementDownloadCount(id);
    }
    
    private String guessFileType(String fileName) {
        if (fileName == null) return "application/octet-stream";
        String lowerName = fileName.toLowerCase();
        
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) return "image/jpeg";
        if (lowerName.endsWith(".png")) return "image/png";
        if (lowerName.endsWith(".gif")) return "image/gif";
        if (lowerName.endsWith(".bmp")) return "image/bmp";
        if (lowerName.endsWith(".webp")) return "image/webp";
        if (lowerName.endsWith(".pdf")) return "application/pdf";
        if (lowerName.endsWith(".txt")) return "text/plain";
        if (lowerName.endsWith(".html") || lowerName.endsWith(".htm")) return "text/html";
        if (lowerName.endsWith(".css")) return "text/css";
        if (lowerName.endsWith(".js")) return "application/javascript";
        if (lowerName.endsWith(".csv")) return "text/csv";
        if (lowerName.endsWith(".json")) return "application/json";
        if (lowerName.endsWith(".md")) return "text/markdown";
        if (lowerName.endsWith(".doc") || lowerName.endsWith(".docx")) return "application/msword";
        if (lowerName.endsWith(".xls") || lowerName.endsWith(".xlsx")) return "application/vnd.ms-excel";
        if (lowerName.endsWith(".ppt") || lowerName.endsWith(".pptx")) return "application/vnd.ms-powerpoint";
        
        return "application/octet-stream";
    }
}
