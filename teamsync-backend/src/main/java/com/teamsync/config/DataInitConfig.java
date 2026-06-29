package com.teamsync.config;

import com.teamsync.entity.User;
import com.teamsync.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 应用启动时初始化：确保所有用户密码正确加密
 */
@Component
public class DataInitConfig implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        List<User> users = userMapper.findAll();
        for (User u : users) {
            // 如果密码不是BCrypt格式（以$2a$开头），则重置为默认密码
            if (u.getPassword() == null || !(u.getPassword().startsWith("$2a$") || u.getPassword().startsWith("$2b$") || u.getPassword().startsWith("$2y$"))) {
                User update = new User();
                update.setId(u.getId());
                update.setPassword(passwordEncoder.encode("123456"));
                userMapper.update(update);
                System.out.println("[初始化] 已重置用户 " + u.getUsername() + " 的密码为默认值 123456");
            }
        }
        System.out.println("[TeamSync] 数据初始化完成，默认密码: 123456");
    }
}
