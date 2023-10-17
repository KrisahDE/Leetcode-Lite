package com.sicko.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zwb
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {

    /**
     * 状态码
     */
    private Integer status;
    /**
     * 判题信息
     */
    private String message;

    /**
     * 占用内存
     */
    private Long memory;

    /**
     * 执行时间
     */
    private Long time;

    /**
     * 结果输出
     */
    private List<String> execResultOutputs;

    /**
     * 错误输出
     */
    private List<String> execErrorOutputs;

}
