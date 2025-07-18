package com.anmory.familymemories.model;

import lombok.Data;

import java.util.Date;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:24
 */

@Data
public class Families {
    private int familyId;
    private String familyName;
    private String familyDescription;
    private Integer creatorId;
    private Date createdAt;
    private Date updatedAt;
}
