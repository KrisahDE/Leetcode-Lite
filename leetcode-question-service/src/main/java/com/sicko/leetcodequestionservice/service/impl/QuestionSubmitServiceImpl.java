package com.sicko.leetcodequestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sicko.commons.common.ErrorCode;
import com.sicko.commons.constant.CommonConstant;
import com.sicko.commons.exception.BusinessException;
import com.sicko.commons.utils.SqlUtils;
import com.sicko.leetcodequestionservice.mapper.QuestionSubmitMapper;
import com.sicko.leetcodequestionservice.rabbitmq.MessageProducer;
import com.sicko.leetcodequestionservice.service.QuestionService;
import com.sicko.leetcodequestionservice.service.QuestionSubmitService;
import com.sicko.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.sicko.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.sicko.model.entity.Question;
import com.sicko.model.entity.QuestionSubmit;
import com.sicko.model.entity.User;
import com.sicko.model.vo.QuestionSubmitVO;
import com.sicko.model.vo.QuestionVO;
import com.sicko.service.JudgeFeignClient;
import com.sicko.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author zwb
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;
    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;
    @Resource
    private UserFeignClient userService;

    @Resource
    private MessageProducer messageProducer;


    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User user) {
        String language = questionSubmitAddRequest.getLanguage();
        if (language == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        // 判断实体是否存在，根据类别获取实体
        final Question question = questionService.getById(questionSubmitAddRequest.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setQuestionId(questionSubmitAddRequest.getQuestionId());
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(questionSubmitAddRequest.getLanguage());
        questionSubmit.setUserId(user.getId());

        boolean isSuccess = this.save(questionSubmit);
        if (!isSuccess) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        //发送信息
        messageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmitId));
//        异步发送请求
//        CompletableFuture.runAsync(() ->{
//            judgeFeignClient.doJudge(questionSubmitId);
//        });
        return questionSubmit.getId();
    }

    @Override
    public void validQuestionSubmit(QuestionSubmit questionSubmit, boolean add) {

    }

    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        //如果请求为空，直接返回queryWrapper
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Integer status = questionSubmitQueryRequest.getStatus();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        queryWrapper.like(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.like(questionId != null, "questionId", questionId);
        queryWrapper.like(status != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionVO getQuestionSubmitVO(QuestionSubmit questionSubmit, HttpServletRequest request) {
        return null;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request) {
        List<QuestionSubmit> questionSubmitsList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitsList)) {
            return questionSubmitVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionSubmitsList.stream().map(QuestionSubmit::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitsList.stream().map(questionSubmit -> {
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
            Long userId = questionSubmit.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionSubmitVO.setUserVO(userService.getUserVO(user));
            return questionSubmitVO;
        }).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}




