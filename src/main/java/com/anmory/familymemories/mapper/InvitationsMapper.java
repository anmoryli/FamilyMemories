package com.anmory.familymemories.mapper;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:47
 */

import com.anmory.familymemories.model.Invitations;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InvitationsMapper {
    /**
     * 创建家庭邀请
     * @param familyId 家庭ID
     * @param inviterId 邀请者ID
     * @param inviteeId 被邀请者ID
     * @param status 邀请状态（pending/accepted/declined）
     * @return 插入记录数，成功返回1
     */
    @Insert("INSERT INTO invitations (family_id, inviter_id, invitee_id, status, created_at, updated_at) " +
            "VALUES (#{familyId}, #{inviterId}, #{inviteeId}, #{status}, NOW(), NOW())")
    int insert(int familyId, int inviterId, int inviteeId, String status);

    /**
     * 根据邀请ID删除邀请
     * @param invitationId 邀请ID
     * @return 删除记录数，成功返回1
     */
    @Delete("DELETE FROM invitations WHERE invitation_id = #{invitationId}")
    int deleteById(int invitationId);

    /**
     * 根据邀请ID查询邀请
     * @param invitationId 邀请ID
     * @return 邀请实体，不存在返回null
     */
    @Select("SELECT * FROM invitations WHERE invitation_id = #{invitationId}")
    Invitations selectById(int invitationId);

    @Select("SELECT * FROM invitations WHERE invitee_id = #{inviteeId}")
    List<Invitations> selectByInviteeId(int inviteeId);

    @Update("UPDATE invitations SET status = #{status}, updated_at = NOW() WHERE invitation_id = #{invitationId}")
    int updateStatus(int invitationId, String status);
}
