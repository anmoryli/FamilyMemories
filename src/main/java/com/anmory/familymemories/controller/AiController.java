package com.anmory.familymemories.controller;

import com.anmory.familymemories.model.UserInfo;
import com.anmory.familymemories.service.AiHistoryService;
import com.anmory.familymemories.service.ToolService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午2:56
 */

@Slf4j
@RestController
@RequestMapping("/ai")
public class AiController {
    @Autowired
    ToolService toolService;
    @Autowired
    AiHistoryService aiHistoryService;

    @RequestMapping("/autoTag")
    public void autoTag(HttpServletRequest request, Integer userIdn) {
        try {
            toolService.tagPhotos(request, userIdn);
        } catch (Exception e) {
            log.error("自动打标签失败", e);
        }
    }

    @RequestMapping("/chat")
    public String chat(String message, HttpServletRequest request, Integer userIdn) {
        UserInfo user = (UserInfo) request.getSession().getAttribute("user");
        if (user == null) {
            log.info("Session中未找到用户信息，使用userIdn: {}", userIdn);
            String response = toolService.chat(message, String.valueOf(userIdn));
            // 保存聊天记录
            aiHistoryService.recordInteraction(userIdn, message, response);
            return response;
        } else {
            log.info("Session中找到用户信息，使用用户ID: {}", user.getUserId());
            String response = toolService.chat(message, String.valueOf(userIdn));
            aiHistoryService.recordInteraction(userIdn, message, response);
            return response;
        }
    }
}
