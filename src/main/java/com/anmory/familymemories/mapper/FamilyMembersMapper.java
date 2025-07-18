package com.anmory.familymemories.mapper;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:45
 */

import com.anmory.familymemories.model.FamilyMembers;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface FamilyMembersMapper {
    /**
     * 添加家庭成员
     * @param familyId 家庭ID
     * @param userId 用户ID
     * @param role 角色（如admin/member）
     * @return 插入记录数，成功返回1
     */
    @Insert("INSERT INTO family_members (family_id, user_id, role, joined_at, updated_at) " +
            "VALUES (#{familyId}, #{userId}, #{role}, NOW(), NOW())")
    int insert(int familyId, int userId, String role);

    /**
     * 根据成员ID删除家庭成员
     * @param familyMemberId 成员ID
     * @return 删除记录数，成功返回1
     */
    @Delete("DELETE FROM family_members WHERE family_member_id = #{arg0}")
    int deleteById(int familyMemberId);

    /**
     * 根据成员ID查询家庭成员
     * @param familyMemberId 成员ID
     * @return 成员实体，不存在返回null
     */
    @Select("SELECT * FROM family_members WHERE family_member_id = #{arg0}")
    FamilyMembers selectById(int familyMemberId);

    /**
     * 根据家庭ID和用户ID查询家庭成员
     * @param familyId 家庭ID
     * @param userId 用户ID
     * @return 成员实体，不存在返回null
     */
    @Select("SELECT * FROM family_members WHERE family_id = #{familyId} AND user_id = #{userId}")
    FamilyMembers selectByFamilyAndUserId(int familyId, int userId);

    /**
     * 更新家庭成员角色
     * @param familyMemberId 成员ID
     * @param role 新角色
     * @return 更新记录数，成功返回1
     */
    @Update("UPDATE family_members SET role = #{role}, updated_at = NOW() WHERE family_member_id = #{familyMemberId}")
    int updateRole(int familyMemberId, String role);

    /**
     * 根据家庭ID删除所有成员
     * @param familyId 家庭ID
     * @return 删除记录数，成功返回大于0
     */
    @Delete("DELETE FROM family_members WHERE family_id = #{familyId}")
    int deleteByFamilyId(int familyId);

    /**
     * 根据用户ID删除所有家庭成员记录
     * @param userId 用户ID
     * @return 删除记录数，成功返回大于0
     */
    @Delete("DELETE FROM family_members WHERE user_id = #{userId}")
    int deleteByUserId(int userId);

    /**
     * 根据家庭ID查询所有成员
     * @param familyId 家庭ID
     * @return 成员列表，可能为空
     */
    @Select("SELECT * FROM family_members WHERE family_id = #{familyId}")
    List<FamilyMembers> selectAllByFamilyId(int familyId);

    @Select("select family.family_members.family_id from family_members where family.family_members.user_id = #{userId}")
    int getFamilyIdByUserId(int userId);
}
