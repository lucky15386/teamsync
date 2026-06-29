package com.teamsync.service.impl;

import com.teamsync.dto.LoginDTO;
import com.teamsync.dto.RegisterDTO;
import com.teamsync.entity.User;
import com.teamsync.mapper.UserMapper;
import com.teamsync.service.AuthService;
import com.teamsync.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("role", user.getRole());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", userInfo);
        return result;
    }

    @Override
    public void register(RegisterDTO dto) {
        // 检查用户名是否已存在
        User existing = userMapper.findByUsername(dto.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("MEMBER");
        user.setStatus(1);

        userMapper.insert(user);
    }
}
