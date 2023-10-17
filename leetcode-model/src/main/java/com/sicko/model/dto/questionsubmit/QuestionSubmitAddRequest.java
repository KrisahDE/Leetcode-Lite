package com.sicko.model.dto.questionsubmit;

import lombok.Data;

import java.io.Serializable;

/**
 * 问题提交请求
 *
 * @author zwb
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;


    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;

}
