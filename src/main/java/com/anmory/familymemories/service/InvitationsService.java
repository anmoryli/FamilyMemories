package com.anmory.familymemories.service;

import com.anmory.familymemories.mapper.InvitationsMapper;
import com.anmory.familymemories.model.Invitations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvitationsService {
    @Autowired
    private InvitationsMapper invitationsMapper;

    /**
     * 创建邀请（参数为被邀请者ID）
     * @return 邀请ID（自增主键）
     */
    public int createInvitation(int familyId, int inviterId, int inviteeId, String status) {
        return invitationsMapper.insert(familyId, inviterId, inviteeId, status);
    }

    /**
     * 删除邀请
     */
    public int deleteInvitation(int invitationId) {
        return invitationsMapper.deleteById(invitationId);
    }

    /**
     * 根据ID查询邀请
     */
    public Invitations getInvitation(int invitationId) {
        return invitationsMapper.selectById(invitationId);
    }

    /**
     * 根据被邀请者ID查询邀请列表
     */
    public List<Invitations> selectByInviteeId(int inviteeId) {
        return invitationsMapper.selectByInviteeId(inviteeId);
    }

    /**
     * 更新邀请状态（pending/accepted/declined）
     */
    public int updateStatus(int invitationId, String status) {
        return invitationsMapper.updateStatus(invitationId, status);
    }
}