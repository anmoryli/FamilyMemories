package com.anmory.familymemories.service;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:57
 */

import com.anmory.familymemories.mapper.FamilyMembersMapper;
import com.anmory.familymemories.model.FamilyMembers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyMembersService {
    @Autowired
    private FamilyMembersMapper familyMembersMapper;

    public int addMember(int familyId, int userId, String role) {
        return familyMembersMapper.insert(familyId, userId, role);
    }

    public int removeMember(int familyMemberId) {
        return familyMembersMapper.deleteById(familyMemberId);
    }

    public FamilyMembers getMember(int familyMemberId) {
        return familyMembersMapper.selectById(familyMemberId);
    }

    public FamilyMembers getMemberByFamilyAndUserId(int familyId, int userId) {
        return familyMembersMapper.selectByFamilyAndUserId(familyId, userId);
    }

    public int updateMemberRole(int familyMemberId, String role) {
        return familyMembersMapper.updateRole(familyMemberId, role);
    }

    public int updateMember(FamilyMembers familyMember) {
        return familyMembersMapper.updateRole(familyMember.getFamilyMemberId(), familyMember.getRole());
    }

    public FamilyMembers getMemberById(int familyMemberId) {
        return familyMembersMapper.selectById(familyMemberId);
    }

    public int addMember(FamilyMembers familyMember) {
        return familyMembersMapper.insert(familyMember.getFamilyId(), familyMember.getUserId(), familyMember.getRole());
    }

    public int deleteMember(int familyMemberId) {
        return familyMembersMapper.deleteById(familyMemberId);
    }

    public FamilyMembers getMemberByFamilyIdAndUserId(int familyId, int userId) {
        return familyMembersMapper.selectByFamilyAndUserId(familyId, userId);
    }

    public List<FamilyMembers> getAllMembersByFamilyId(int familyId) {
        return familyMembersMapper.selectAllByFamilyId(familyId);
    }

    public int getFamilyIdByUserId(int userId) {
        return familyMembersMapper.getFamilyIdByUserId(userId);
    }

    public int deleteByFamilyId(int familyId) {
        return familyMembersMapper.deleteByFamilyId(familyId);
    }
}
