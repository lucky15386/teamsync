package com.teamsync.controller;

import com.teamsync.common.Result;
import com.teamsync.entity.Document;
import com.teamsync.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentService documentService;

    private static final List<String> PREVIEWABLE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp",
            "application/pdf", "text/plain", "text/html", "text/css",
            "application/javascript", "text/csv", "application/json"
    );

    /**
     * 上传文档
     */
    @PostMapping("/upload")
    public Result<Document> upload(@RequestParam Long projectId,
                                   @RequestParam(required = false) String category,
                                   @RequestParam("file") MultipartFile file,
                                   HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            logger.info("用户 {} 开始上传文件: {}, 大小: {} bytes", userId, file.getOriginalFilename(), file.getSize());
            Document doc = documentService.upload(projectId, userId, category, file);
            logger.info("文件上传成功: {}", doc.getName());
            return Result.success("上传成功", doc);
        } catch (Exception e) {
            logger.error("文件上传失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取项目文档列表
     */
    @GetMapping("/project/{projectId}")
    public Result<List<Document>> findByProjectId(@PathVariable Long projectId) {
        return Result.success(documentService.findByProjectId(projectId));
    }

    /**
     * 获取所有文档
     */
    @GetMapping
    public Result<List<Document>> findAll() {
        return Result.success(documentService.findAll());
    }

    /**
     * 获取文档详情
     */
    @GetMapping("/{id}")
    public Result<Document> findById(@PathVariable Long id) {
        return Result.success(documentService.findById(id));
    }

    /**
     * 下载文档
     */
    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response) throws Exception {
        Document doc = documentService.findById(id);
        if (doc == null) {
            response.setStatus(404);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":404,\"message\":\"文档不存在\"}");
            return;
        }

        Path filePath = Paths.get(doc.getFilePath());
        if (!Files.exists(filePath)) {
            response.setStatus(404);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":404,\"message\":\"文件不存在\"}");
            return;
        }

        // 增加下载次数
        documentService.incrementDownloadCount(id);

        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        String encodedName = URLEncoder.encode(doc.getName(), "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedName);
        response.setContentLengthLong(Files.size(filePath));

        // 写出文件流
        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        }
    }

    /**
     * 在线预览文档
     */
    @GetMapping("/preview/{id}")
    public void preview(@PathVariable Long id, HttpServletResponse response) throws Exception {
        Document doc = documentService.findById(id);
        if (doc == null) {
            response.setStatus(404);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":404,\"message\":\"文档不存在\"}");
            return;
        }

        Path filePath = Paths.get(doc.getFilePath());
        if (!Files.exists(filePath)) {
            response.setStatus(404);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":404,\"message\":\"文件不存在\"}");
            return;
        }

        // 智能判断内容类型，不再严格限制
        String contentType = doc.getFileType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }

        // 根据文件扩展名推断类型，如果 mime 类型未知
        String fileName = doc.getName();
        String guessedType = guessContentType(fileName);
        if (!isValidContentType(contentType) && guessedType != null) {
            contentType = guessedType;
        }

        // 设置响应头
        response.setContentType(contentType);
        response.setCharacterEncoding("UTF-8");
        response.setContentLengthLong(Files.size(filePath));

        // 写出文件流
        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        }
    }

    private boolean isValidContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            return false;
        }
        return contentType.contains("/");
    }

    private String guessContentType(String fileName) {
        if (fileName == null) return null;
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
        
        return "application/octet-stream";
    }

    /**
     * 删除文档
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        documentService.deleteById(id);
        return Result.success("删除成功", null);
    }
}
