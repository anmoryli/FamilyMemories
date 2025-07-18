package com.anmory.familymemories.service;

import com.anmory.familymemories.mapper.FamiliesMapper;
import com.anmory.familymemories.model.Families;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:55
 */

@Service
public class FamiliesService {
    @Autowired
    private FamiliesMapper familiesMapper;

    public int createFamily(String familyName, String familyDescription, int creatorId) {
        return familiesMapper.insert(familyName, familyDescription, creatorId);
    }

    public int deleteFamily(int familyId) {
        return familiesMapper.deleteById(familyId);
    }

    public Families getFamily(int familyId) {
        return familiesMapper.selectById(familyId);
    }

    public int updateFamily(int familyId, String familyName, String familyDescription) {
        return familiesMapper.update(familyId, familyName, familyDescription);
    }

    public int getFamilyIdByFamilyNameAndFamilyDescription(String familyName, String familyDescription) {
        return familiesMapper.getFamilyIdByFamilyNameAndFamilyDescription(familyName, familyDescription);
    }
}