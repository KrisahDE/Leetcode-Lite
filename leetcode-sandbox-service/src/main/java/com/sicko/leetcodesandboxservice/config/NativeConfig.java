package com.sicko.leetcodesandboxservice.config;

import com.sicko.leetcodesandboxservice.condition.JavaNativeSandboxCondition;
import com.sicko.leetcodesandboxservice.service.CodeSandboxService;
import com.sicko.leetcodesandboxservice.service.impl.JavaNativeCodeSandboxServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author zwb
 */
@Configuration
@Conditional(JavaNativeSandboxCondition.class)
public class NativeConfig {
    @Bean
    public CodeSandboxService codeSandbox(){
        System.out.println("本地代码沙箱");
        return new JavaNativeCodeSandboxServiceImpl();
    }
}
