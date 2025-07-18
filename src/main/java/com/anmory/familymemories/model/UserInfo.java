package com.anmory.familymemories.model;

import lombok.Data;

import java.util.Date;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:22
 */

@Data
public class UserInfo {
    private int userId;
    private String username;
    private String password;
    private String email;
    private Date createdAt;
    private Date updatedAt;
}