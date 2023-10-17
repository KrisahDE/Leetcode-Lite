package com.sicko.leetcodequestionservice.controller.inner;

import com.sicko.leetcodequestionservice.service.QuestionService;
import com.sicko.leetcodequestionservice.service.QuestionSubmitService;
import com.sicko.model.entity.Question;
import com.sicko.model.entity.QuestionSubmit;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author zwb
 *
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController {
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @GetMapping("/submit/get/id")
    public QuestionSubmit getSubmitById(@RequestParam("questionSubmitId") Long questionSubmitId){
        return questionSubmitService.getById(questionSubmitId);

    }

    @GetMapping("/get/id")
    public Question getById(@RequestParam("questionId") Long questionId){
        return questionService.getById(questionId);
    }


    @PostMapping("/submit/update/id")
    public boolean updateSubmitById(@RequestBody QuestionSubmit questionSubmit){
        return questionSubmitService.updateById(questionSubmit);
    }
}
