package com.anmory.familymemories.controller;

import com.anmory.familymemories.model.Comments;
import com.anmory.familymemories.model.UserInfo;
import com.anmory.familymemories.service.CommentsService;
import com.anmory.familymemories.service.PhotosService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {
    @Autowired
    private CommentsService commentsService;
    @Autowired
    private PhotosService photosService;

    /**
     * 添加评论
     */
    @PostMapping("/add")
    public int addComment(@RequestParam int photoId,
                          @RequestParam String content,
                          @RequestParam(required = false) Integer userIdn,
                          HttpServletRequest request) {
        int userId = resolveUserId(request, userIdn);
        if (userId <= 0) {
            log.error("添加评论失败：无效的用户ID");
            return -1;
        }

        if (photosService.getPhoto(photoId) == null) {
            log.error("添加评论失败：照片ID={}不存在", photoId);
            return -1;
        }

        int commentId = commentsService.addComment(photoId, userId, content);
        if (commentId > 0) {
            log.info("评论添加成功：评论ID={}, 照片ID={}, 用户ID={}",
                    commentId, photoId, userId);
            return commentId;
        } else {
            log.error("评论添加失败：照片ID={}, 用户ID={}", photoId, userId);
            return -1;
        }
    }

    @RequestMapping("/getUsernameByCommentId")
    public String getUsernameByCommentId(@RequestParam int commentId) {
        Comments comment = commentsService.getComment(commentId);
        if (comment == null) {
            log.error("获取用户名失败：评论ID={}不存在", commentId);
            return null;
        }

        String username = commentsService.getUserByCommentId(commentId);
        if (username == null) {
            log.error("获取用户名失败：评论ID={}对应的用户不存在", commentId);
            return null;
        }

        log.info("获取用户名成功：评论ID={}, 用户名={}", commentId, username);
        return username;
    }

    /**
     * 删除评论
     */
    @PostMapping("/delete")
    public boolean deleteComment(@RequestParam int commentId,
                                 @RequestParam(required = false) Integer userIdn,
                                 HttpServletRequest request) {
        int userId = resolveUserId(request, userIdn);
        if (userId <= 0) {
            log.error("删除评论失败：无效的用户ID");
            return false;
        }

        Comments comment = commentsService.getComment(commentId);
        if (comment == null) {
            log.error("删除评论失败：评论ID={}不存在", commentId);
            return false;
        }

        if (comment.getUserId() != userId) {
            log.error("删除评论失败：无权限（评论所有者ID={}, 当前用户ID={}",
                    comment.getUserId(), userId);
            return false;
        }

        int result = commentsService.deleteComment(commentId);
        if (result > 0) {
            log.info("评论删除成功：评论ID={}, 用户ID={}", commentId, userId);
            return true;
        } else {
            log.error("评论删除失败：评论ID={}", commentId);
            return false;
        }
    }

    /**
     * 获取照片的评论列表
     * @param photoId 照片ID
     * @return 评论列表
     */
    @GetMapping("/list")
    public List<Comments> getCommentsByPhotoId(
            @RequestParam int photoId) {

        if (photosService.getPhoto(photoId) == null) {
            log.error("获取评论失败：照片ID={}不存在", photoId);
            return List.of(); // 返回空列表而非null，便于前端处理
        }

        List<Comments> comments = commentsService.getCommentsByPhotoId(photoId);
        log.info("获取照片评论成功：照片ID={}, 评论数量={}", photoId, comments.size());
        return comments;
    }

    /**
     * 解析用户ID
     */
    private int resolveUserId(HttpServletRequest request, Integer userIdn) {
        if (userIdn != null && userIdn > 0) {
            return userIdn;
        }

        UserInfo user = (UserInfo) request.getSession().getAttribute("session_user_key");
        return user != null ? user.getUserId() : -1;
    }
}