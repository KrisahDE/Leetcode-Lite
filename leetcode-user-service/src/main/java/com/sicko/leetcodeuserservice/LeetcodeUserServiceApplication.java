package com.sicko.leetcodeuserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zwb
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.sicko.service"})
@SpringBootApplication()
@MapperScan("com.sicko.leetcodeuserService.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.sicko")
public class LeetcodeUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeetcodeUserServiceApplication.class, args);
    }

}
