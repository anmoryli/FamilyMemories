package com.anmory.familymemories.controller;

import com.anmory.familymemories.model.UserInfo;
import com.anmory.familymemories.service.UserInfoService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午1:05
 */

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserInfoService userInfoService;
    @RequestMapping("/login")
    public UserInfo login(String username, String password, HttpSession session) {
        if(username == null || password == null) {
            log.error("Username or password is null");
            return null;
        }
        UserInfo userInfo = userInfoService.getUserByUsername(username);
        if(userInfo == null) {
            log.error("User not found: " + username);
            return null;
        }
        if(!userInfo.getPassword().equals(password)) {
            log.error("Password is incorrect: " + username);
            return null;
        }
        session.setAttribute("session_user_key", userInfo);
        log.info("User logged in successfully: " + username);
        return userInfo;
    }

    @RequestMapping("/getUserByUsername")
    public UserInfo getUserByUsername(String username) {
        UserInfo userInfo = userInfoService.getUserByUsername(username);
        if(userInfo == null) {
            log.error("User not found: " + username);
            return null;
        }
        log.info("User found: " + username);
        return userInfo;
    }

    @RequestMapping("/logout")
    public boolean logout(HttpSession session) {
        if(session.getAttribute("session_user_key") == null) {
            log.error("No user is logged in");
            return false;
        }
        session.removeAttribute("session_user_key");
        log.info("User logged out successfully");
        return true;
    }

    @RequestMapping("/register")
    public boolean register(String username, String password, String email) {
        if(username == null || password == null) {
            log.error("Username or password is null");
            return false;
        }
        UserInfo existingUser = userInfoService.getUserByUsername(username);
        if(existingUser != null) {
            log.error("Username already exists: " + username);
            return false;
        }
        userInfoService.insertUser(username, password, email);
        log.info("User registered successfully: " + username);
        return true;
    }
}
