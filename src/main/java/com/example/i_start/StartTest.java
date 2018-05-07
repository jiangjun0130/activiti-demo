package com.example.i_start;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import java.io.InputStream;

public class StartTest {

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();


    /**
     * 部署流程定义（从zip）
     */
    @Test
    public void deployProcessDefinition_inputStream() {
        InputStream inputStreamBpmn = this.getClass().getResourceAsStream("start.bpmn");
        InputStream inputStreamPng = this.getClass().getResourceAsStream("start.png");
        Deployment deploy = processEngine.getRepositoryService()    // 与流程定义和部署相关的service
                .createDeployment() // 创建部署对象
                .name("开始结束")   // 添加部署名称
                //.addZipInputStream(new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("diagrams/helloworld.zip")))
                .addInputStream("start.bpmn", inputStreamBpmn)
                .addInputStream("start.png", inputStreamPng)
                .deploy();

        System.out.println("部署ID：" + deploy.getId());
        System.out.println("部署名称：" + deploy.getName());
    }

    /**
     * 启动流程实例+判断流程是否结束+查询历史
     */
    @Test
    public void startProcessInstance() {
        String processDefinitionKey = "start ";
        ProcessInstance processInstance = processEngine.getRuntimeService()   // 与正在执行的流程实例和执行对象相关的service
                // 使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中Id属性
                // 使用key启动，默认使用最新版本的流程启动
                .startProcessInstanceByKey(processDefinitionKey);
        System.out.println("流程实例ID:" + processInstance.getId());    // 流程实例ID
        System.out.println("流程定义ID:" + processInstance.getProcessDefinitionId());   // 流程定义ID

        /** 判断流程是否结束，查询正在执行的执行对象表 */
        ProcessInstance singleResult = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        // 说明流程实例结束了
        if(singleResult == null){
            //查询历史，获取l流程的相关信息
            HistoricProcessInstance historicProcessInstance = processEngine.getHistoryService()
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstance.getId())
                    .singleResult();
            System.out.println(historicProcessInstance.getId() + "  " + historicProcessInstance.getStartTime() + "  "
            + historicProcessInstance.getEndTime() + "  " + historicProcessInstance.getDurationInMillis());
        }
    }




}
