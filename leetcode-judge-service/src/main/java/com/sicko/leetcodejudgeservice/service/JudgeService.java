package com.sicko.leetcodejudgeservice.service;


import com.sicko.model.entity.QuestionSubmit;

/**
 * @author zwb
 */
public interface JudgeService {
    /**
     * 判题逻辑
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);

}
