package com.teamsync.controller;

import com.teamsync.common.Result;
import com.teamsync.entity.OperationLog;
import com.teamsync.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/operation-logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    public Result<List<OperationLog>> findAll(HttpServletRequest request) {
        requireAdmin(request);
        return Result.success(operationLogService.findAll());
    }

    private void requireAdmin(HttpServletRequest request) {
        if (!"ADMIN".equals(request.getAttribute("role"))) {
            throw new RuntimeException("无权限访问");
        }
    }
}
