package com.sicko.leetcodesandboxservice.service.impl;


import com.sicko.model.entity.ExecuteCodeRequest;
import com.sicko.model.entity.ExecuteCodeResponse;
import org.springframework.stereotype.Service;

/**
 * @author zwb
 */
public class JavaNativeCodeSandboxServiceImpl extends JavaCodeSandboxServiceTemplate {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest ExecuteCodeRequest) {
        return super.executeCode(ExecuteCodeRequest);
    }
}
