package com.teamsync.service.impl;

import com.teamsync.dto.PasswordChangeDTO;
import com.teamsync.dto.UserUpdateDTO;
import com.teamsync.entity.User;
import com.teamsync.mapper.UserMapper;
import com.teamsync.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public void update(Long id, UserUpdateDTO dto) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (dto.getRealName() != null) user.setRealName(dto.getRealName());
        if (dto.getRole() != null) user.setRole(dto.getRole());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userMapper.update(user);
    }

    @Override
    public void changePassword(Long id, PasswordChangeDTO dto) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 6) {
            throw new RuntimeException("新密码至少6位");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.update(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        userMapper.updateStatus(id, status);
    }

    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }
}
