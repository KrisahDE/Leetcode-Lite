package com.sicko.model.dto.questionsubmit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 判题结果信息
 *
 * @author zwb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JudgeInfo {
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
}
