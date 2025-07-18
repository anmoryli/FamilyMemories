package com.anmory.familymemories.mapper;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:46
 */

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PhotoMilestonesMapper {
    /**
     * 关联照片与里程碑
     * @param photoId 照片ID
     * @param milestoneId 里程碑ID
     * @return 插入记录数，成功返回1
     */
    @Insert("INSERT INTO photo_milestones (photo_id, milestone_id, created_at, updated_at) " +
            "VALUES (#{photoId}, #{milestoneId}, NOW(), NOW())")
    int insert(int photoId, int milestoneId);

    /**
     * 解除照片与里程碑的关联
     * @param photoId 照片ID
     * @param milestoneId 里程碑ID
     * @return 删除记录数，成功返回1
     */
    @Delete("DELETE FROM photo_milestones WHERE photo_id = #{photoId} AND milestone_id = #{milestoneId}")
    int delete(int photoId, int milestoneId);

    /**
     * 根据里程碑ID查询所有关联的照片ID
     * @param milestoneId 里程碑ID
     * @return 照片ID列表
     */
    @Select("SELECT photo_id FROM photo_milestones WHERE milestone_id = #{milestoneId}")
    List<Integer> selectPhotoIdsByMilestoneId(int milestoneId);
}
