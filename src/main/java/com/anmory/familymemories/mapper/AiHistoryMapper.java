package com.anmory.familymemories.mapper;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:51
 */

import com.anmory.familymemories.model.AiHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AiHistoryMapper {
    /**
     * 记录AI交互历史
     * @param userId 用户ID
     * @param query 用户查询内容
     * @param response AI回复内容
     * @return 插入记录数，成功返回1
     */
    @Insert("INSERT INTO ai_history (user_id, query, response, created_at, updated_at) " +
            "VALUES (#{userId}, #{query}, #{response}, NOW(), NOW())")
    int insert(int userId, String query, String response);

    /**
     * 根据历史ID删除交互记录
     * @param hisId 历史ID
     * @return 删除记录数，成功返回1
     */
    @Delete("DELETE FROM ai_history WHERE his_id = #{hisId}")
    int deleteById(int hisId);

    /**
     * 根据历史ID查询交互记录
     * @param hisId 历史ID
     * @return 历史记录实体，不存在返回null
     */
    @Select("SELECT * FROM ai_history WHERE his_id = #{hisId}")
    AiHistory selectById(int hisId);

    /**
     * 根据用户ID查询最新的AI交互记录
     * @param userId 用户ID
     * @return 最新的AI交互记录实体，不存在返回null
     */
    @Select("SELECT * FROM ai_history WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT 1")
    AiHistory selectLatestByUserId(int userId);

    /**
     * 根据用户ID删除所有AI交互记录
     * @param userId 用户ID
     * @return 删除记录数，成功返回大于0
     */
    @Delete("DELETE FROM ai_history WHERE user_id = #{userId}")
    int deleteByUserId(int userId);

    /**
     * 根据用户ID查询所有AI交互记录
     * @param userId 用户ID
     * @return AI交互记录列表，可能为空
     */
    @Select("SELECT * FROM ai_history WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<AiHistory> selectAllByUserId(int userId);
}
