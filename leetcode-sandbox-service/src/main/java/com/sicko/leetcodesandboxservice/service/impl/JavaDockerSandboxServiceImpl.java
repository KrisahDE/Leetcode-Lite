package com.sicko.leetcodesandboxservice.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.sicko.model.entity.ExecuteCodeRequest;
import com.sicko.model.entity.ExecuteCodeResponse;
import com.sicko.model.entity.ExecuteMessage;
import com.sicko.model.enums.SandBoxExecuteMessageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zwb
 */
@Slf4j
public class JavaDockerSandboxServiceImpl extends JavaCodeSandboxServiceTemplate {
    private String GLOBAL_CODE_DIR = "F:\\LeetCodeLite\\code-sandbox\\code";
    private long TIME_OUT = 5000L;
    private StopWatch stopWatch = new StopWatch();
    public static final boolean FIRST_INIT = true;
    String IMAGE_NAME = "openjdk:8-alpine";


    @Override
    public ExecuteCodeResponse runClass(ExecuteCodeRequest ExecuteCodeRequest, File file) {
        String userCodeParentPath = file.getParentFile().getAbsolutePath();
        //3.创建docker容器
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        //拉取镜像
        if (FIRST_INIT){
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(IMAGE_NAME);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback(){
                @Override
                public void onNext(PullResponseItem item) {
                    log.info("拉取镜像："+item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd
                        .exec(pullImageResultCallback)
                        .awaitCompletion();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("拉取镜像异常");
                throw new RuntimeException();
            }
            pullImageCmd.close();
        }
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(IMAGE_NAME);



        //4.将代码放入容器中
        HostConfig hostConfig = new HostConfig();
        //绑定共享文件夹
        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app")));


        //限制内存100Mb、限制内存交换、限制CPU数量
        hostConfig
                .withMemory(100 * 1000 * 1000L)
                .withMemorySwap(0L)
                .withCpuCount(1L);

        //创建容器并配置hostConfig，禁止docker访问网络、根目录，设置标准输入输出
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withReadonlyRootfs(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();
        String containerId = createContainerResponse.getId();
        containerCmd.close();


        //启动容器
        dockerClient.startContainerCmd(containerId).exec();


        //5.执行编译后的字节码文件
        List<String> inputs = ExecuteCodeRequest.getInput();
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs :inputs) {

            //5.1创建新的执行Java命令的进程，但进程并未运行
            String[] inputArgsArray = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();

            ExecuteMessage executeMessage = new ExecuteMessage();
            final String[] message = {null};
            final String[] errorMessage = {null};
            final long[] time = {0L};
            final boolean[] isTimeout = {true};
            final long[] maxMemory = {0L};


            //5.2 创建执行进程的回调函数
            String execId = execCreateCmdResponse.getId();
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onComplete() {
                    isTimeout[0] = false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    if (StreamType.STDERR.equals(streamType)) {
                        errorMessage[0] = new String(frame.getPayload());
                    } else {
                        message[0] = new String(frame.getPayload());
                    }
                    super.onNext(frame);
                }
            };



            //5.3 获取内存占用
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onNext(Statistics statistics) {
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(),maxMemory[0]);

                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void close() throws IOException {

                }
            });
            statsCmd.exec(statisticsResultCallback);

            //5.4 运行5.1中创建的进程
            try {
                stopWatch.start();
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        //限制运行时间
                        .awaitCompletion(TIME_OUT, TimeUnit.MICROSECONDS);
                stopWatch.stop();
                statsCmd.close();
                time[0] = stopWatch.getLastTaskTimeMillis();
            } catch (InterruptedException e) {
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }


            //6.获取运行结果
            executeMessage.setMessage(message[0]);
            executeMessage.setErrorMessage(errorMessage[0]);
            executeMessage.setMaxMemory(maxMemory[0]);
            executeMessage.setTime(time[0]);
            executeMessageList.add(executeMessage);

        }

        //7.将运行结果封装并返回
        ExecuteCodeResponse ExecuteCodeResponse = new ExecuteCodeResponse();
        List<Long> timeList = new ArrayList<>();
        List<Long> memoryList = new ArrayList<>();
        for (ExecuteMessage e:executeMessageList) {
            ExecuteCodeResponse.getExecResultOutputs().add(e.getMessage());
            ExecuteCodeResponse.getExecErrorOutputs().add(e.getErrorMessage());
            timeList.add(e.getTime());
            memoryList.add(e.getMaxMemory());
        }
        if (inputs.size() == ExecuteCodeResponse.getExecResultOutputs().size() && ExecuteCodeResponse.getExecErrorOutputs().isEmpty()){
            ExecuteCodeResponse.setMessage(SandBoxExecuteMessageEnum.ACCEPTED.getValue());
            ExecuteCodeResponse.setStatus(SandBoxExecuteMessageEnum.ACCEPTED.getStatus());
        }else {
            ExecuteCodeResponse.setMessage(SandBoxExecuteMessageEnum.RUNTIME_ERROR.getValue());
        }
        if (!timeList.isEmpty() && !memoryList.isEmpty()){
            Long time = 0L;
            for (Long l:timeList) {
                time = time + l;
            }
            time = time/timeList.size();
            Long memory = 0L;
            for (Long l:memoryList) {
                memory = memory + l;
            }
            memory = memory/memoryList.size();
            ExecuteCodeResponse.setMemory(memory);
            ExecuteCodeResponse.setTime(time);
        }

        return ExecuteCodeResponse;
    }


}
