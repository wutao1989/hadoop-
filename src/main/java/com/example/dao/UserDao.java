package com.example.dao;

import com.example.entities.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao {

    //查询单个信息
    User selectUserByName(@Param("name") String name);


}
