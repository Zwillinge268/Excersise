package com.kahiroshi.service.impl;

import com.kahiroshi.mapper.LoginMapper;
import com.kahiroshi.object.User;
import com.kahiroshi.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginMapper loginMapper;

    @Override
    public User login(User userData) {
        return loginMapper.findUserByUsernameAndPassword(userData);
    }
}
