package com.sicko.leetcodejudgeservice.service.impl;

import cn.hutool.json.JSONUtil;
import com.sicko.commons.common.ErrorCode;
import com.sicko.commons.exception.BusinessException;
import com.sicko.leetcodejudgeservice.service.JudgeService;
import com.sicko.model.dto.question.JudgeCase;
import com.sicko.model.dto.question.JudgeConfig;
import com.sicko.model.dto.questionsubmit.JudgeInfo;
import com.sicko.model.entity.ExecuteCodeRequest;
import com.sicko.model.entity.ExecuteCodeResponse;
import com.sicko.model.entity.Question;
import com.sicko.model.entity.QuestionSubmit;
import com.sicko.model.enums.QuestionSubmitStatusEnum;
import com.sicko.model.enums.SandBoxExecuteMessageEnum;
import com.sicko.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zwb
 */
@Slf4j
@Service
public class JudgeServiceImpl implements JudgeService {
    @Value("${sandbox.type:native}")
    private String type;

    @Resource
    private CodeSandboxFeignClient codeSandboxFeignClient;

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Override
    /**
     * 1.根据题目提交信息获取题目提交记录，题目信息等
     * 2.如果题目提交状态为判题中，直接返回中断执行
     * 3.更改题目提交信息为判题中
     * 4.获取输入用例交给沙箱运行
     * 5.根据沙箱输出执行判题逻辑
     * 6.更改题目提交信息为相应状态和判题结果
     */
    public QuestionSubmit doJudge(long questionSubmitId) {
        //1.根据题目提交信息获取题目提交记录，题目信息等
        QuestionSubmit questionSubmit = questionFeignClient.getSubmitById(questionSubmitId);
        Question question = questionFeignClient.getById(questionSubmit.getQuestionId());
        String code = questionSubmit.getCode();
        String language = questionSubmit.getLanguage();
        String judgeCase = question.getJudgeCase();
        List<JudgeCase> judgeCases = new ArrayList<>();
        //获取判题用例
        try {
            judgeCases = JSONUtil.toList(judgeCase, JudgeCase.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("判题用例解析失败：" + judgeCase);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "判题用例解析失败：" + judgeCase);
        }

        //获取输入输出用例
        List<String> inputExamples = new ArrayList<>();
        List<String> outputExamples = new ArrayList<>();
        for (int i = 0; i < judgeCases.size(); i++) {
            inputExamples.add(judgeCases.get(i).getInput());
            outputExamples.add(judgeCases.get(i).getOutput());
        }

        String judgeConfig = question.getJudgeConfig();
        JudgeConfig judgeConfigObj = JSONUtil.toBean(judgeConfig, JudgeConfig.class);


        Integer status = questionSubmit.getStatus();

        //2.如果题目提交状态为判题中，直接返回中断执行
        if (status != null && status.intValue() != QuestionSubmitStatusEnum.WAITING.getValue()) {
            throw new BusinessException(ErrorCode.REPEATED_SUBMIT, "重复提交判题请求");
        }

        //3.更改题目提交信息为判题中
        questionSubmit.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean isSuccess = questionFeignClient.updateSubmitById(questionSubmit);
        if (!isSuccess) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据更新失败");
        }

        //4.获取输入用例交给沙箱运行
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .input(inputExamples)
                .language(language)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandboxFeignClient.executeCode(executeCodeRequest);

        //5.根据沙箱输出执行判题逻辑
        //5.1 如果编译或者运行失败，直接返回
        //5.2 判断内存是否超过限制
        //5.3 判断时间是否超过限制
        //5.4 判断返回结果是否和输出用例相等

        JudgeInfo judgeInfo = JudgeInfo.builder()
                .time(executeCodeResponse.getTime())
                .memory(executeCodeResponse.getMemory())
                .build();

        //5.1 如果编译失败，直接返回
        if (executeCodeResponse.getStatus().equals(SandBoxExecuteMessageEnum.COMPILE_ERROR.getStatus())){
            return buildQuestionSubmit(
                    QuestionSubmitStatusEnum.FAILED.getValue(),
                    executeCodeResponse.getMessage(),
                    questionSubmit,
                    judgeInfo);
        }

        //5.2 如果运行失败，直接返回
        if (!executeCodeResponse.getStatus().equals(SandBoxExecuteMessageEnum.ACCEPTED.getStatus())){
            return buildQuestionSubmit(
                    QuestionSubmitStatusEnum.FAILED.getValue(),
                    executeCodeResponse.getMessage(),
                    questionSubmit,
                    judgeInfo);
        }
        //5.3 判断内存是否超过限制
        if (executeCodeResponse.getMemory() != null && executeCodeResponse.getMemory() > judgeConfigObj.getMemoryLimit()){
            return buildQuestionSubmit(
                    QuestionSubmitStatusEnum.FAILED.getValue(),
                    "内存占用过多",
                    questionSubmit,
                    judgeInfo);
        }
        //5.4 判断时间是否超过限制
        if (executeCodeResponse.getTime() != null && executeCodeResponse.getTime() > judgeConfigObj.getTimeLimit()){
            return buildQuestionSubmit(
                    QuestionSubmitStatusEnum.FAILED.getValue(),
                    "时间占用过多",
                    questionSubmit,
                    judgeInfo);
        }
        // 5.5 判断返回结果是否和输出用例相等
        if (executeCodeResponse.getExecResultOutputs().size() != judgeCases.size()){
            return buildQuestionSubmit(
                    QuestionSubmitStatusEnum.FAILED.getValue(),
                    SandBoxExecuteMessageEnum.RUNTIME_ERROR.getValue(),
                    questionSubmit,
                    judgeInfo);
        }
        List<String> execResultOutputs = executeCodeResponse.getExecResultOutputs();
        for (int i = 0; i < judgeCases.size(); i++) {
            if (!execResultOutputs.get(i).equals(outputExamples.get(i))){
                return buildQuestionSubmit(
                        QuestionSubmitStatusEnum.FAILED.getValue(),
                        SandBoxExecuteMessageEnum.RUNTIME_ERROR.getValue(),
                        questionSubmit,
                        judgeInfo);
            }
        }

        return buildQuestionSubmit(
                QuestionSubmitStatusEnum.SUCCEED.getValue(),
                QuestionSubmitStatusEnum.SUCCEED.getText(),
                questionSubmit,
                judgeInfo);

    }

    public QuestionSubmit buildQuestionSubmit(Integer status, String message,QuestionSubmit questionSubmit,JudgeInfo judgeInfo){
        questionSubmit.setStatus(status);
        judgeInfo.setMessage(message);
        questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        boolean isSuccess = questionFeignClient.updateSubmitById(questionSubmit);
        if (!isSuccess) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据更新失败");
        }
        return questionSubmit;
    }
}
