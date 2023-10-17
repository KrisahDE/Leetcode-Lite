package com.sicko.service;


import com.sicko.model.entity.Question;
import com.sicko.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 问题服务
 *
 * @author zwb
 */
@FeignClient(name = "leetcode-question-service", path = "/api/question/inner")
public interface QuestionFeignClient {

    @GetMapping("/get/id")
    Question getById(@RequestParam("questionId") Long questionId);

    @GetMapping("/submit/get/id")
    QuestionSubmit getSubmitById(@RequestParam("questionSubmitId") Long questionSubmitId);

    @PostMapping("/submit/update/id")
    boolean updateSubmitById(@RequestBody QuestionSubmit questionSubmit);
}
