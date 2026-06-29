package com.teamsync;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.teamsync.mapper")
public class TeamSyncApplication {
    public static void main(String[] args) {
        SpringApplication.run(TeamSyncApplication.class, args);
    }
}
