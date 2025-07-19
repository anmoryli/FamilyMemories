package com.anmory.familymemories.service;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午1:04
 */

import com.anmory.familymemories.mapper.CommentsMapper;
import com.anmory.familymemories.model.Comments;
import com.anmory.familymemories.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsService {
    @Autowired
    private CommentsMapper commentsMapper;

    public int addComment(int photoId, int userId, String content) {
        return commentsMapper.insert(photoId, userId, content);
    }

    public int deleteComment(int commentId) {
        return commentsMapper.deleteById(commentId);
    }

    public Comments getComment(int commentId) {
        return commentsMapper.selectById(commentId);
    }

    public List<Comments> getCommentsByPhotoId(int photoId) {
        return commentsMapper.selectByPhotoId(photoId);
    }

    public String getUserByCommentId(int commentId) {
        return commentsMapper.getUsernameByCommentId(commentId);
    }
}
