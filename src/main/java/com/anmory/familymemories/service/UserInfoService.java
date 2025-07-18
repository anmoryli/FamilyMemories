package com.anmory.familymemories.service;

import com.anmory.familymemories.mapper.UserInfoMapper;
import com.anmory.familymemories.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:53
 */

@Service
public class UserInfoService {
    @Autowired
    UserInfoMapper userInfoMapper;

    public UserInfo getUserByUsername(String username) {
        return userInfoMapper.getUserByUsername(username);
    }

    public UserInfo getUserById(int userId) {
        return userInfoMapper.getUserById(userId);
    }

    public int insertUser(String username, String password, String email) {
        return userInfoMapper.insert(username, password, email);
    }

    public int deleteUserById(int userId) {
        return userInfoMapper.deleteById(userId);
    }

    public int updateUser(UserInfo userInfo) {
        return userInfoMapper.update(userInfo.getUserId(), userInfo.getUsername(), userInfo.getPassword(), userInfo.getEmail());
    }

    public UserInfo selectUserById(int userId) {
        return userInfoMapper.selectById(userId);
    }

    public int getFamilyIdByUserId(int userId) {
        return userInfoMapper.getFamilyIdByUserId(userId);
    }
}
