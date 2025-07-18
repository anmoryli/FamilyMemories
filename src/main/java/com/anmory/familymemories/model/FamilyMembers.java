package com.anmory.familymemories.model;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:25
 */

import lombok.Data;

import java.util.Date;

@Data
public class FamilyMembers {
    private int familyMemberId;
    private int familyId;
    private int userId;
    private String role;
    private Date joinedAt;
    private Date updatedAt;
}
