package com.anmory.familymemories.model;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:26
 */

import lombok.Data;

import java.util.Date;

@Data
public class Invitations {
    private int invitationId;
    private int familyId;
    private int inviterId;
    private int inviteeId;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
