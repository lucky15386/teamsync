package com.teamsync.mapper;
import com.teamsync.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    User findById(@Param("id") Long id);
    List<User> findAll();
    int insert(User user);
    int update(User user);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int deleteById(@Param("id") Long id);
}
