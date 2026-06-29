package com.teamsync.controller;

import com.teamsync.common.Result;
import com.teamsync.dto.PasswordChangeDTO;
import com.teamsync.dto.UserUpdateDTO;
import com.teamsync.entity.User;
import com.teamsync.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.findById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return Result.success(user);
    }

    /**
     * 获取用户选项（用于添加项目成员等场景）
     */
    @GetMapping("/options")
    public Result<List<User>> findOptions() {
        List<User> users = userService.findAll();
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping
    public Result<List<User>> findAll(HttpServletRequest request) {
        requireAdmin(request);
        List<User> users = userService.findAll();
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserUpdateDTO dto, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        if (!id.equals(currentUserId) && !"ADMIN".equals(role)) {
            throw new RuntimeException("无权限修改该用户信息");
        }
        userService.update(id, dto);
        return Result.success("更新成功", null);
    }

    @PutMapping("/{id}/password")
    public Result<Void> changePassword(@PathVariable Long id, @RequestBody PasswordChangeDTO dto,
                                       HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (!id.equals(currentUserId)) {
            throw new RuntimeException("只能修改自己的密码");
        }
        userService.changePassword(id, dto);
        return Result.success("密码修改成功", null);
    }

    /**
     * 更新用户状态（启用/禁用）
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        requireAdmin(request);
        userService.updateStatus(id, status);
        return Result.success("状态更新成功", null);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id, HttpServletRequest request) {
        requireAdmin(request);
        userService.deleteById(id);
        return Result.success("删除成功", null);
    }

    private void requireAdmin(HttpServletRequest request) {
        if (!"ADMIN".equals(request.getAttribute("role"))) {
            throw new RuntimeException("无权限访问");
        }
    }
}
