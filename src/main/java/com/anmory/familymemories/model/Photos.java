package com.anmory.familymemories.model;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:25
 */

import lombok.Data;

import java.util.Date;

@Data
public class Photos {
    private int photoId;
    private int familyId;
    private int uploaderId;
    private String filePath;
    private String title;
    private String description;
    private String tag;
    private Date takeTime;
    private String takeAt;
    private String cameraParameters;
    private Date uploadedAt;
    private Date updatedAt;
}
