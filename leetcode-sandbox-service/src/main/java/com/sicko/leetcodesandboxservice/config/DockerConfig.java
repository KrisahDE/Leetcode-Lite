package com.sicko.leetcodesandboxservice.config;

import com.sicko.leetcodesandboxservice.condition.JavaDockerSandboxCondition;
import com.sicko.leetcodesandboxservice.service.CodeSandboxService;
import com.sicko.leetcodesandboxservice.service.impl.JavaDockerSandboxServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author zwb
 */
@Configuration
@Conditional(JavaDockerSandboxCondition.class)
public class DockerConfig {
    @Bean
    public CodeSandboxService codeSandbox(){
        System.out.println("Docker代码沙箱");
        return new JavaDockerSandboxServiceImpl();
    }
}
