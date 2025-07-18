package com.anmory.familymemories.service;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午1:02
 */

import com.anmory.familymemories.mapper.PhotoMilestonesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoMilestonesService {
    @Autowired
    private PhotoMilestonesMapper photoMilestonesMapper;

    public int associatePhotoWithMilestone(int photoId, int milestoneId) {
        return photoMilestonesMapper.insert(photoId, milestoneId);
    }

    public int disassociatePhotoFromMilestone(int photoId, int milestoneId) {
        return photoMilestonesMapper.delete(photoId, milestoneId);
    }

    /**
     * 获取里程碑关联的所有照片ID
     */
    public List<Integer> getPhotoIdsByMilestoneId(int milestoneId) {
        return photoMilestonesMapper.selectPhotoIdsByMilestoneId(milestoneId);
    }
}
