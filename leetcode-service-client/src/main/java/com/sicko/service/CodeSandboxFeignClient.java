package com.sicko.service;


import com.sicko.model.entity.ExecuteCodeRequest;
import com.sicko.model.entity.ExecuteCodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zwb
 */
@FeignClient(name = "leetcode-sandbox-service", path = "/api/sandbox/inner")
public interface CodeSandboxFeignClient {

    /**
     * 执行代码
     * @param ExecuteCodeRequest
     * @return
     */
    @PostMapping("/do")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest ExecuteCodeRequest);



}
