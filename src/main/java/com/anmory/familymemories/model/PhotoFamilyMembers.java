package com.anmory.familymemories.model;

import lombok.Data;

import java.util.Date;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午4:03
 */

@Data
public class PhotoFamilyMembers {
    private Integer photoId;
    private Integer familyMemberId;
    private Date createdAt;
    private Date updatedAt;
}
