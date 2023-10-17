package com.sicko.service;

import com.sicko.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author zwb
 */
@FeignClient(name = "leetcode-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient {
    /**
     * 判题逻辑
     * @param questionSubmitId
     * @return
     */
    @PostMapping("/do")
    QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId);

}
