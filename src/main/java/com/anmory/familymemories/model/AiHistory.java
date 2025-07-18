package com.anmory.familymemories.model;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:27
 */

import lombok.Data;

import java.util.Date;

@Data
public class AiHistory {
    private int hisId;
    private int userId;
    private String query;
    private String response;
    private Date createdAt;
    private Date updatedAt;
}
