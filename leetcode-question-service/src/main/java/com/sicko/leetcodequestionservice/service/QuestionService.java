package com.sicko.leetcodequestionservice.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sicko.model.dto.question.QuestionQueryRequest;
import com.sicko.model.entity.Question;
import com.sicko.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;


/**
 * 问题服务
 *
 * @author zwb
 */
public interface QuestionService extends IService<Question> {
    /**
     * 校验题目
     *
     * @param question 问题
     * @param add      是否为创建状态
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取包装类
     *
     * @param questionQueryRequest 问题查询参数
     * @return 包装类
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目VO
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

}
