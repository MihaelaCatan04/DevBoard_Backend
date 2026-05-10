package com.devboard.warzone.mapper;

import com.devboard.warzone.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);

    void insert(User user);

    boolean existsByUsername(@Param("username") String username);
}