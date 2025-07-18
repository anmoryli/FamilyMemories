package com.anmory.familymemories.model;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:27
 */

import lombok.Data;

import java.util.Date;

@Data
public class Comments {
    private int commentId;
    private int photoId;
    private int userId;
    private String content;
    private Date createdAt;
    private Date updatedAt;
}
