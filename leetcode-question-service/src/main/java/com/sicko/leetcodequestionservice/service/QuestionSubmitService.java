package com.sicko.leetcodequestionservice.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sicko.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.sicko.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.sicko.model.entity.QuestionSubmit;
import com.sicko.model.entity.User;
import com.sicko.model.vo.QuestionSubmitVO;
import com.sicko.model.vo.QuestionVO;


import javax.servlet.http.HttpServletRequest;

/**
 * 问题提交服务
 *
 * @author zwb
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 处理问题提交
     *
     * @param questionSubmitAddRequest
     * @param user
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User user);

    /**
     * 校验题目
     *
     * @param questionSubmit 问题
     * @param add      是否为创建状态
     */
    void validQuestionSubmit(QuestionSubmit questionSubmit, boolean add);

    /**
     * 获取包装类
     *
     * @param questionSubmitQueryRequest 问题查询参数
     * @return 包装类
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目VO
     *
     * @param questionSubmit
     * @param request
     * @return
     */
    QuestionVO getQuestionSubmitVO(QuestionSubmit questionSubmit, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param request
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request);

}
