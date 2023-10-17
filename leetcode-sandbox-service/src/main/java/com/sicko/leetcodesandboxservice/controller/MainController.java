package com.sicko.leetcodesandboxservice.controller;

import com.sicko.leetcodesandboxservice.service.CodeSandboxService;
import com.sicko.model.entity.ExecuteCodeRequest;
import com.sicko.model.entity.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zwb
 */
@RestController
@RequestMapping("/inner")
public class MainController {
    @Resource
    private CodeSandboxService codeSandboxService;
    @PostMapping("/do")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeRequest, HttpServletRequest request,
                                    HttpServletResponse response){

        if (executeRequest == null){
            throw new RuntimeException();
        }
        ExecuteCodeResponse executeResponse = codeSandboxService.executeCode(executeRequest);
        return executeResponse;
    }
}
