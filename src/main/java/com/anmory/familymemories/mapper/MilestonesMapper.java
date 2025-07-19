package com.anmory.familymemories.mapper;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:45
 */

import com.anmory.familymemories.model.Milestones;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MilestonesMapper {
    /**
     * 创建家庭里程碑
     * @param familyId 所属家庭ID
     * @param title 里程碑标题
     * @param description 里程碑描述
     * @param eventDate 事件日期
     * @return 插入记录数，成功返回1
     */
    @Insert("INSERT INTO milestones (family_id, title, description, event_date, created_at, updated_at) " +
            "VALUES (#{familyId}, #{title}, #{description}, #{eventDate}, NOW(), NOW())")
    int insert(int familyId, String title, String description, java.util.Date eventDate);

    /**
     * 根据里程碑ID删除里程碑
     * @param milestoneId 里程碑ID
     * @return 删除记录数，成功返回1
     */
    @Delete("DELETE FROM milestones WHERE milestone_id = #{milestoneId}")
    int deleteById(int milestoneId);

    /**
     * 根据里程碑ID查询里程碑
     * @param milestoneId 里程碑ID
     * @return 里程碑实体，不存在返回null
     */
    @Select("SELECT * FROM milestones WHERE milestone_id = #{milestoneId}")
    Milestones selectById(int milestoneId);

    /**
     * 根据家庭ID查询所有里程碑
     * @param familyId 家庭ID
     * @return 里程碑列表，可能为空
     */
    @Select("SELECT * FROM milestones WHERE family_id = #{familyId} ORDER BY event_date DESC")
    List<Milestones> selectAllByFamilyId(int familyId);
}
