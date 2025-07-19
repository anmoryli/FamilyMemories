package com.anmory.familymemories.mapper;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:50
 */

import com.anmory.familymemories.model.Comments;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentsMapper {
    /**
     * 添加照片评论
     * @param photoId 照片ID
     * @param userId 用户ID
     * @param content 评论内容
     * @return 插入记录数，成功返回1
     */
    @Insert("INSERT INTO comments (photo_id, user_id, content, created_at, updated_at) " +
            "VALUES (#{photoId}, #{userId}, #{content}, NOW(), NOW())")
    int insert(int photoId, int userId, String content);

    /**
     * 根据评论ID删除评论
     * @param commentId 评论ID
     * @return 删除记录数，成功返回1
     */
    @Delete("DELETE FROM comments WHERE comment_id = #{commentId}")
    int deleteById(int commentId);

    /**
     * 根据评论ID查询评论
     * @param commentId 评论ID
     * @return 评论实体，不存在返回null
     */
    @Select("SELECT * FROM comments WHERE comment_id = #{commentId}")
    Comments selectById(int commentId);

    /**
     * 根据照片ID查询所有评论
     * @param photoId 照片ID
     * @return 评论列表，可能为空
     */
    @Select("SELECT * FROM comments WHERE photo_id = #{photoId}")
    List<Comments> selectByPhotoId(int photoId);

    @Select("SELECT u.username FROM comments c " +
            "JOIN user_info u ON c.user_id = u.user_id " +
            "WHERE c.comment_id = #{commentId}")
    String getUsernameByCommentId(int commentId);
}
