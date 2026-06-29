package com.teamsync.service.impl;

import com.teamsync.entity.OperationLog;
import com.teamsync.mapper.OperationLogMapper;
import com.teamsync.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public List<OperationLog> findAll() {
        return operationLogMapper.findAll();
    }

    @Override
    public void log(Long userId, String username, String operation, String method, String ip) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation(operation);
        log.setMethod(method);
        log.setIp(ip);
        operationLogMapper.insert(log);
    }
}
