package com.example.service;

import com.example.entities.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserService {

    //查询单个信息
    User selectUserByName(@Param("name") String name);

}
