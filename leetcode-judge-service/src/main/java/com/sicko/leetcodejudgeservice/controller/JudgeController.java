package com.sicko.leetcodejudgeservice.controller;

import com.sicko.leetcodejudgeservice.service.JudgeService;
import com.sicko.model.entity.QuestionSubmit;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zwb
 */
@RestController
@RequestMapping("/inner")
public class JudgeController {

    @Resource
    private JudgeService judgeService;

    @RequestMapping("/do")
    public QuestionSubmit doJudge(@RequestBody long questionSubmitId){
        return judgeService.doJudge(questionSubmitId);
    }
}
