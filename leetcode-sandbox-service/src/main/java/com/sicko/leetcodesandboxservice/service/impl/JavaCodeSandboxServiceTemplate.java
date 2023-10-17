package com.sicko.leetcodesandboxservice.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.sicko.leetcodesandboxservice.service.CodeSandboxService;
import com.sicko.model.entity.ExecuteCodeRequest;
import com.sicko.model.entity.ExecuteCodeResponse;
import com.sicko.model.enums.SandBoxExecuteMessageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zwb
 */
@Slf4j
public abstract class JavaCodeSandboxServiceTemplate implements CodeSandboxService {
    private String GLOBAL_CODE_DIR = "F:\\LeetCodeLite\\code-sandbox\\code";
    private long TIME_OUT = 5000L;
    private final static String FILE_NAME = "Main.java";
    private static String userCodeParentPath;
    private StopWatch stopWatch = new StopWatch();


    /**
     * 运行代码
     *
     * @param ExecuteCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest ExecuteCodeRequest) {
        //1.将代码写入文件
        File file = writeFile(ExecuteCodeRequest);

        //2.编译文件
        ExecuteCodeResponse compileResponse = compileFile(file);
        if (compileResponse.getStatus().equals(SandBoxExecuteMessageEnum.COMPILE_ERROR.getStatus())) {
            return compileResponse;
        }

        //3.运行字节码
        ExecuteCodeResponse ExecuteCodeResponse = runClass(ExecuteCodeRequest,file);

        //4.删除文件
        delFile();

        return ExecuteCodeResponse;
    }
    public File writeFile(ExecuteCodeRequest ExecuteCodeRequest){
        //获取参数
        String code = ExecuteCodeRequest.getCode();
        //若文件夹不存在则新建文件夹
        if (!FileUtil.exist(GLOBAL_CODE_DIR)) {
            FileUtil.mkdir(GLOBAL_CODE_DIR);
        }

        //将代码写入文件
        userCodeParentPath = GLOBAL_CODE_DIR + File.separator + UUID.randomUUID().toString();
        String javaFilePath = userCodeParentPath + File.separator + FILE_NAME;
        File file = new File(javaFilePath);
        FileUtil.writeString(code, file, StandardCharsets.UTF_8);
        return file;
    }
    public ExecuteCodeResponse compileFile(File file){
        String compileCommand = String.format("javac -encoding utf-8 %s", file.getAbsolutePath());
        ExecuteCodeResponse compileResponse = executeCommand(compileCommand,false);
        if (!compileResponse.getStatus().equals(SandBoxExecuteMessageEnum.ACCEPTED.getStatus())) {

            compileResponse.setStatus(SandBoxExecuteMessageEnum.COMPILE_ERROR.getStatus());
            compileResponse.setMessage(SandBoxExecuteMessageEnum.COMPILE_ERROR.getValue());
        }
        return compileResponse;
    }
    public ExecuteCodeResponse runClass(ExecuteCodeRequest ExecuteCodeRequest,File file){
        List<String> inputArgs = ExecuteCodeRequest.getInput();
        String exeCommand = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
        ExecuteCodeResponse execResponse = executeCommand(exeCommand,true);
        return execResponse;
    }
    public void delFile(){
        if (FileUtil.exist(userCodeParentPath)){
            FileUtil.del(userCodeParentPath);
        }
    }

    /**
     * 执行命令
     *
     * @param command
     */
    public ExecuteCodeResponse executeCommand(String command,boolean isExec) {
        ExecuteCodeResponse res = new ExecuteCodeResponse();
        try {
            stopWatch.start();
            Process process = Runtime.getRuntime().exec(command);
            if(isExec){
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        process.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
            int exeValue = process.waitFor();
            stopWatch.stop();
            List<String> output = getProcessOutput(process, false);
            //巡行错误
            if (exeValue != 0) {
                output.addAll(getProcessOutput(process, true));
                res.setExecErrorOutputs(output);
                res.setStatus(SandBoxExecuteMessageEnum.RUNTIME_ERROR.getStatus());
                res.setMessage(SandBoxExecuteMessageEnum.RUNTIME_ERROR.getValue());
            } else {
                res.setExecResultOutputs(output);
                res.setTime(stopWatch.getLastTaskTimeMillis());
                res.setStatus(SandBoxExecuteMessageEnum.ACCEPTED.getStatus());
                res.setMessage(SandBoxExecuteMessageEnum.ACCEPTED.getValue());
            }
            return res;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 获取运行过程输出
     *
     * @param process
     * @param isErrorOutput
     * @return
     */
    public List<String> getProcessOutput(Process process, boolean isErrorOutput) {
        List<String> outputLines = new ArrayList<>();
        try {
            BufferedReader bufferedReader;
            if (isErrorOutput) {
                bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            }

            String outputLine;
            while ((outputLine = bufferedReader.readLine()) != null) {
                outputLines.add(outputLine);
            }
            return outputLines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
