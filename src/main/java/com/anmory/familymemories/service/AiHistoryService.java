package com.anmory.familymemories.service;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午1:04
 */

import com.anmory.familymemories.mapper.AiHistoryMapper;
import com.anmory.familymemories.model.AiHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiHistoryService {
    @Autowired
    private AiHistoryMapper aiHistoryMapper;

    public int recordInteraction(int userId, String query, String response) {
        return aiHistoryMapper.insert(userId, query, response);
    }

    public int deleteInteraction(int hisId) {
        return aiHistoryMapper.deleteById(hisId);
    }

    public AiHistory getInteraction(int hisId) {
        return aiHistoryMapper.selectById(hisId);
    }

    public List<AiHistory> getAllInteractionsByUserId(int userId) {
        return aiHistoryMapper.selectAllByUserId(userId);
    }
}
