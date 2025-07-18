package com.anmory.familymemories.service;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午1:00
 */

import com.anmory.familymemories.mapper.MilestonesMapper;
import com.anmory.familymemories.model.Milestones;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MilestonesService {
    @Autowired
    private MilestonesMapper milestonesMapper;

    public int createMilestone(int familyId, String title, String description, java.util.Date eventDate) {
        return milestonesMapper.insert(familyId, title, description, eventDate);
    }

    public int deleteMilestone(int milestoneId) {
        return milestonesMapper.deleteById(milestoneId);
    }

    public Milestones getMilestone(int milestoneId) {
        return milestonesMapper.selectById(milestoneId);
    }

    public List<Milestones> getAllMilestonesByFamilyId(int familyId) {
        return milestonesMapper.selectAllByFamilyId(familyId);
    }
}
