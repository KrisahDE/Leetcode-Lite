package com.sicko.leetcodesandboxservice.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author zwb
 */
public class JavaDockerSandboxCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        // 检查application.yml中的属性是否等于"a"
        String propertyValue = environment.getProperty("sandbox.type");
        return "docker".equals(propertyValue);
    }
}
