package com.example.service.impl;

import com.example.dao.UserDao;
import com.example.entities.User;
import com.example.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;


    @Override
    public User selectUserByName(String name) {
        return userDao.selectUserByName(name);
    }
}
