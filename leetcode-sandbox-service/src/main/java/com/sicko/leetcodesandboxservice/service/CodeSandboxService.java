package com.sicko.leetcodesandboxservice.service;


import com.sicko.model.entity.ExecuteCodeRequest;
import com.sicko.model.entity.ExecuteCodeResponse;

/**
 * @author zwb
 */
public interface CodeSandboxService {

    /**
     * 执行代码
     * @param ExecuteCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest ExecuteCodeRequest);



}
