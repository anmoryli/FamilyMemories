package com.anmory.familymemories.service;

import com.anmory.familymemories.mapper.PhotoFamilyMembersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午4:06
 */

@Service
public class PhotoFamilyMembersService {
    @Autowired
    PhotoFamilyMembersMapper photoFamilyMembersMapper;

    public int addPhotoFamilyMember(int photoId, int userId) {
        return photoFamilyMembersMapper.insertPhotoFamilyMember(photoId, userId);
    }
}
