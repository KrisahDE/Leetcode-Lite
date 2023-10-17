package com.sicko.model.enums;

import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zwb
 */
public enum SandBoxExecuteMessageEnum {

    //运行成功
    ACCEPTED("Accepted", "运行成功",1),
    RUNTIME_ERROR("Runtime Error", "运行错误",2),
    COMPILE_ERROR("Compile Error", "编译错误",3),
    TIME_LIMIT_EXCEEDED("Time Limit Exceeded", "运行超时",4),
    SYSTEM_ERROR("System Error", "系统错误",5);

    private final String text;

    private final String value;

    private Integer status;

    SandBoxExecuteMessageEnum(String text, String value, Integer status) {
        this.text = text;
        this.value = value;
        this.status = status;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static SandBoxExecuteMessageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (SandBoxExecuteMessageEnum anEnum : SandBoxExecuteMessageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }
    public Integer getStatus() {
        return status;
    }

    public String getText() {
        return text;
    }
}
