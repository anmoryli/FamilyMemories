package com.anmory.familymemories.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Anmory/李梦杰
 * @description TODO
 * @date 2025-07-18 上午4:04
 */

@Mapper
public interface PhotoFamilyMembersMapper {
    // Define methods for interacting with the photo_family_members table
    // For example, you might have methods like:

    @Insert("INSERT INTO photo_family_members (photo_id, user_id) VALUES (#{photoId}, #{userId})")
    int insertPhotoFamilyMember(int photoId, int userId);
    // int deletePhotoFamilyMember(int photoId, int familyMilestoneId);
    // PhotoFamilyMembers selectPhotoFamilyMember(int photoId, int familyMilestoneId);
    // List<PhotoFamilyMembers> selectAllPhotoFamilyMembers();

}
