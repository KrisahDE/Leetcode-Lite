package com.sicko.leetcodejudgeservice;

import com.sicko.leetcodejudgeservice.rabbitmq.MqInitMain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.sicko.service"})
@SpringBootApplication()
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.sicko")
public class LeetcodeJudgeServiceApplication {

    public static void main(String[] args) {
        MqInitMain.doInit();
        SpringApplication.run(LeetcodeJudgeServiceApplication.class, args);
    }

}
