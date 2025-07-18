package com.anmory.familymemories.service;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.anmory.familymemories.model.Photos;
import com.anmory.familymemories.model.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ai.openai.OpenAiChatModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午2:38
 */

@Slf4j
@Service
public class ToolService {
    @Autowired
    PhotosService photosService;
    @Autowired
    FamilyMembersService familyMembersService;

    public String tagSelector(String url)
            throws ApiException, NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage systemMessage = MultiModalMessage.builder().role(Role.SYSTEM.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("text", "You are a helpful assistant."))).build();
        String prompt = "请根据图片内容为照片打上合适的标签。" +
                "请注意，标签应简洁明了，能够准确描述照片内容。"
                + "如果图片中没有明显的内容或无法识别，请返回'none'。" +
                "你需要从以下标签种选择一个，只能选一个：\n" +
                "1. 家庭聚会\n" +
                "2. 旅行\n" +
                "3. 生日庆祝\n" +
                "4. 婚礼\n" +
                "5. 日常生活\n" +
                "6. 节日庆祝\n" +
                "7. 运动\n" +
                "8. none\n" +
                "9. 艺术\n" +
                "10. 文化\n" +
                "11. 自然风光\n" +
                "12. 宠物\n" +
                "13. 朋友聚会\n" +
                "14. 学校活动\n" +
                "15. 工作场景\n" +
                "你只能回答标签，不能回答数字和小数点，只需要返回标签内容，不需要其他任何文字。";
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("image", url),
                        Collections.singletonMap("text", "图中描绘的是什么景象?"))).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .model("qwen-vl-max-latest")  // 此处以qwen-vl-max-latest为例，可按需更换模型名称。模型列表：https://help.aliyun.com/model-studio/getting-started/models
                .messages(Arrays.asList(systemMessage, userMessage))
                .build();
        MultiModalConversationResult result = conv.call(param);
        return result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text").toString();
    }

    private final OpenAiChatModel openAiChatModel;

    public ToolService(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }
    private final ChatMemory chatMemory = new InMemoryChatMemory();

    public String chat(@RequestParam(value = "message",defaultValue = "你是谁") String message,
                               @RequestParam String sessionId) {
        var messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory,sessionId,100);
        ChatClient chatClient = ChatClient.builder(openAiChatModel)
                .defaultSystem("你是一个家庭记忆助手，帮助用户记录和回忆家庭中的重要时刻。" +
                        "你的回答只能是纯文本，严禁任何markdown格式的回答，" +
                        "你的回答必须人性化，不能太官方笼统，必须具体到人能理解")
                .defaultAdvisors(messageChatMemoryAdvisor)
                .build();
        return chatClient.prompt()
                .user(message)
                .advisors(messageChatMemoryAdvisor)
                .call()
                .content();
    }

    public int getId(HttpServletRequest request, int userIdn) {
        // 获取家庭id
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("session_user_key");
        if (userInfo != null) {
            return userInfo.getUserId();
        } else if (userIdn > 0) {
            return userIdn;
        } else {
            throw new IllegalArgumentException("无法获取有效的用户ID");
        }
    }

    public String tagPhotos(HttpServletRequest request, int userIdn) throws NoApiKeyException, InputRequiredException, UploadFileException {
        // 获取家庭id
        int id = getId(request, userIdn);
        int familyId = familyMembersService.getFamilyIdByUserId(id);
        List<Photos> photos = photosService.selectByFamilyId(familyId);
        String result = "none";
        for( Photos photo : photos) {
            result = tagSelector("http://anmory.com:8091"+photo.getFilePath());
            System.out.println(JsonUtils.toJson(result));
            if (result == null || result.isEmpty()) {
                // 如果没有识别到有效标签，返回提示信息
                log.error("未能识别到有效标签，请重新尝试。");
                return "未能识别到有效标签，请重新尝试。";
            }
        }
        // 这里可以添加逻辑来处理标签，例如保存到数据库或返回给用户
        List<Integer> photoIds = notTaged(familyId);
        for( Integer photoId : photoIds) {
            // 为每张照片添加标签
            photosService.addTagToPhoto(photoId, result);
        }
        log.info("已成功识别标签：{}", result);
        return "已成功识别标签：" + result;
    }

    public List<Integer> notTaged(int familyId) {
        // 这里可以添加逻辑来检查家庭ID下的所有照片是否都已打上标签
        // 例如，查询数据库中该家庭ID下的所有照片，并检查每张照片的标签字段是否为空
        // 如果所有照片都有标签，则返回true，否则返回false
        // 目前返回true作为示例
        List<Photos> photos = photosService.selectByFamilyId(familyId);
        List<Integer> untaggedPhotoIds = new ArrayList<>();
        for(Photos photo : photos) {
            if (photo.getTag().equals("none") || photo.getTag().isEmpty()) {
                // 如果有照片标签是none，那么就把这个照片的id添加到列表中
                // 这里可以返回一个包含未打标签照片ID的列表
                untaggedPhotoIds.add(photo.getPhotoId());
                return untaggedPhotoIds;
            }
        }
        return null;
    }

    // 把String类型的Date转换成java.util.Date
    public java.util.Date stringToDate(String dateString) {
        try {
            return java.sql.Date.valueOf(dateString);
        } catch (IllegalArgumentException e) {
            log.error("日期格式错误: {}", dateString, e);
            return null; // 或者抛出异常
        }
    }
}
