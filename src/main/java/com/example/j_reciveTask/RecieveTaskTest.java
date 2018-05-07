package com.example.j_reciveTask;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import java.io.InputStream;

public class RecieveTaskTest {

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();


    /**
     * 部署流程定义（从zip）
     */
    @Test
    public void deployProcessDefinition_inputStream() {
        InputStream inputStreamBpmn = this.getClass().getResourceAsStream("receiveTask.bpmn");
        InputStream inputStreamPng = this.getClass().getResourceAsStream("receiveTask.png");
        Deployment deploy = processEngine.getRepositoryService()    // 与流程定义和部署相关的service
                .createDeployment() // 创建部署对象
                .name("接受任务活动")   // 添加部署名称
                //.addZipInputStream(new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("diagrams/helloworld.zip")))
                .addInputStream("receiveTask.bpmn", inputStreamBpmn)
                .addInputStream("receiveTask.png", inputStreamPng)
                .deploy();

        System.out.println("部署ID：" + deploy.getId());
        System.out.println("部署名称：" + deploy.getName());
    }

    /**
     * 启动流程实例+设置流程变量+获取流程变量+向后执行一步
     */
    @Test
    public void startProcessInstance() {
        String processDefinitionKey = "receiveTask";
        ProcessInstance processInstance = processEngine.getRuntimeService()   // 与正在执行的流程实例和执行对象相关的service
                // 使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中Id属性
                // 使用key启动，默认使用最新版本的流程启动
                .startProcessInstanceByKey(processDefinitionKey);
        System.out.println("流程实例ID:" + processInstance.getId());    // 流程实例ID
        System.out.println("流程定义ID:" + processInstance.getProcessDefinitionId());   // 流程定义ID

        // 查询执行对象id
        Execution execution = processEngine.getRuntimeService()
                .createExecutionQuery()
                .processInstanceId(processInstance.getId()) // 流程实例id
                .activityId("_3")   // 当前活动id，对于bpmn文件中的活动节点的id属性
                .singleResult();

        // 使用流程变量设置当日销售额，用来传递业务参数
        processEngine.getRuntimeService()
                .setVariable(execution.getId(),  "汇总当日销售额", 100000);

        // 向后执行一步，如果流程处于等待状态，使得流程继续执行
        processEngine.getRuntimeService()
                .signal(execution.getId());

        // 从流程变量中获取当日汇总销售额
        Execution execution2 = processEngine.getRuntimeService()
                .createExecutionQuery()
                .processInstanceId(processInstance.getId()) // 流程实例id
                .activityId("_4")   // 当前活动id，对于bpmn文件中的活动节点的id属性
                .singleResult();

        Integer value = (Integer) processEngine.getRuntimeService()
                .getVariable(execution2.getId(), "汇总当日销售额");
        System.out.println("给老板发送短信，汇总当日销售额：" + value);

        // 向后执行一步，如果流程处于等待状态，使得流程继续执行
        processEngine.getRuntimeService()
                .signal(execution2.getId());
    }

}
