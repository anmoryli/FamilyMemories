package com.anmory.familymemories.model;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:26
 */

import lombok.Data;

import java.util.Date;

@Data
public class PhotoMilestones {
    private int photoId;
    private int milestoneId;
    private Date createdAt;
    private Date updatedAt;
}
