package com.sicko.model.entity;

import lombok.Data;

/**
 * @author zwb
 */
@Data
public class ExecuteMessage {
    private String message;
    private String errorMessage;
    private Long time;
    private Long maxMemory;
}
