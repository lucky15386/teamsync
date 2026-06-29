package com.teamsync.service;

import com.teamsync.dto.PasswordChangeDTO;
import com.teamsync.dto.UserUpdateDTO;
import com.teamsync.entity.User;
import java.util.List;

public interface UserService {
    User findById(Long id);
    List<User> findAll();
    User findByUsername(String username);
    void update(Long id, UserUpdateDTO dto);
    void changePassword(Long id, PasswordChangeDTO dto);
    void updateStatus(Long id, Integer status);
    void deleteById(Long id);
}
