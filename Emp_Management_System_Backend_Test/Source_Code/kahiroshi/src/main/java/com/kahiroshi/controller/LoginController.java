package com.kahiroshi.controller;

import com.kahiroshi.object.Result;
import com.kahiroshi.object.User;
import com.kahiroshi.service.LoginService;
import com.kahiroshi.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * Login with username and password
     * */
    @PostMapping
    public Result login(@RequestBody User userData) {
        User user = loginService.login(userData);

        if (user != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("username", user.getUsername());

            String jwtKey = JwtUtils.generateKey(claims);
            return Result.success(jwtKey);
        }

        return Result.error("Not Login");
    }
}
