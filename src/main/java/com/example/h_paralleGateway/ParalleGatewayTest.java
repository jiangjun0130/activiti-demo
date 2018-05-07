package com.example.h_paralleGateway;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParalleGatewayTest {

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();


    /**
     * 部署流程定义（从zip）
     */
    @Test
    public void deployProcessDefinition_inputStream() {
        InputStream inputStreamBpmn = this.getClass().getResourceAsStream("paralleGateway.bpmn");
        InputStream inputStreamPng = this.getClass().getResourceAsStream("paralleGateway.png");
        Deployment deploy = processEngine.getRepositoryService()    // 与流程定义和部署相关的service
                .createDeployment() // 创建部署对象
                .name("并行网关")   // 添加部署名称
                //.addZipInputStream(new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("diagrams/helloworld.zip")))
                .addInputStream("paralleGateway.bpmn", inputStreamBpmn)
                .addInputStream("paralleGateway.png", inputStreamPng)
                .deploy();

        System.out.println("部署ID：" + deploy.getId());
        System.out.println("部署名称：" + deploy.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstance() {
        String processDefinitionKey = "paralleGateway ";
        ProcessInstance processInstance = processEngine.getRuntimeService()   // 与正在执行的流程实例和执行对象相关的service
                // 使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中Id属性
                // 使用key启动，默认使用最新版本的流程启动
                .startProcessInstanceByKey(processDefinitionKey);
        System.out.println("流程实例ID:" + processInstance.getId());    // 流程实例ID
        System.out.println("流程定义ID:" + processInstance.getProcessDefinitionId());   // 流程定义ID
    }


    /**
     * 完成任务的同时，设置流程变量
     */
    @Test
    public void completeMyTask(){
        String taskId = "192502";
        // 完成任务的同时，设置流程变量 。使用流程变量来指定完成 任务后，下一个连线对于sequenceFlow.bpmn中的表达式${message=='不重要'}

        processEngine.getTaskService()  // 与正在执行的任务管理的service
                .complete(taskId);
        System.out.println("完成任务！任务ID：" + taskId);
    }

}
