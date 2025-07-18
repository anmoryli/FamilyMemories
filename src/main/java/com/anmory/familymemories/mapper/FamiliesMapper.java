package com.anmory.familymemories.mapper;

/**
 * @author Anmory/李梦杰
 * @description TODO
 * @date 2025-07-18 上午12:43
 */
import com.anmory.familymemories.model.Families;
import org.apache.ibatis.annotations.*;

@Mapper
public interface FamiliesMapper {
    /**
     * 创建新家庭
     * @param familyName 家庭名称
     * @param familyDescription 家庭描述
     * @param creatorId 创建者ID
     * @return 插入记录数，成功返回1
     */
    @Insert("INSERT INTO families (family_name, family_description, creator_id, created_at, updated_at) " +
            "VALUES (#{familyName}, #{familyDescription}, #{creatorId}, NOW(), NOW())")
    int insert(String familyName, String familyDescription, int creatorId);

    /**
     * 根据家庭ID删除家庭
     * @param familyId 家庭ID
     * @return 删除记录数，成功返回1
     */
    @Delete("DELETE FROM families WHERE family_id = #{arg0}")
    int deleteById(int familyId);

    /**
     * 根据家庭ID查询家庭
     * @param familyId 家庭ID
     * @return 家庭实体，不存在返回null
     */
    @Select("SELECT * FROM families WHERE family_id = #{arg0}")
    Families selectById(int familyId);

    /**
     * 更新家庭信息
     * @param familyId 家庭ID
     * @param familyName 新的家庭名称
     * @param familyDescription 新的家庭描述
     * @return 更新记录数，成功返回1
     */
    @Update("UPDATE families SET " +
            "family_name = #{arg1}, " +
            "family_description = #{arg2}, " +
            "updated_at = NOW() " +
            "WHERE family_id = #{arg0}")
    int update(int familyId, String familyName, String familyDescription);

    @Select("SELECT * FROM families WHERE family_name = #{familyName} AND family_description = #{familyDescription}")
    int getFamilyIdByFamilyNameAndFamilyDescription( String familyName, String familyDescription);
}
