package com.teamsync.service;

import com.teamsync.dto.LoginDTO;
import com.teamsync.dto.RegisterDTO;
import java.util.Map;

public interface AuthService {
    Map<String, Object> login(LoginDTO dto);
    void register(RegisterDTO dto);
}
