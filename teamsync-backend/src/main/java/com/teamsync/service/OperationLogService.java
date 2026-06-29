package com.teamsync.service;

import com.teamsync.entity.OperationLog;

import java.util.List;

public interface OperationLogService {
    List<OperationLog> findAll();

    void log(Long userId, String username, String operation, String method, String ip);
}
