package com.sicko.leetcodesandboxservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.sicko")
public class LeetcodeSandboxServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeetcodeSandboxServiceApplication.class, args);
    }

}
