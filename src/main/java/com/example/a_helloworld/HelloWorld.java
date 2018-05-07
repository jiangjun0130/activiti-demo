package com.example.a_helloworld;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.List;


public class HelloWorld {

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    /**
     * 部署流程定义
     */
    @Test
    public void deployProcessDefinition() {
        Deployment deploy = processEngine.getRepositoryService()    // 与流程定义和部署相关的service
                .createDeployment() // 创建部署对象
                .name("helloworld入门2")   // 添加部署名称
                .addClasspathResource("diagrams/helloworld.bpmn") // 从classpath资源加载，一次只能加载一个文件
                .addClasspathResource("diagrams/helloworld.png") // 从classpath资源加载，一次只能加载一个文件
                .deploy();

        System.out.println("部署ID：" + deploy.getId());
        System.out.println("部署名称：" + deploy.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstance() {
        String processDefinitionKey = "helloworld";
        ProcessInstance processInstance = processEngine.getRuntimeService()   // 与正在执行的流程实例和执行对象相关的service
                // 使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中Id属性
                // 使用key启动，默认使用最新版本的流程启动
                .startProcessInstanceByKey(processDefinitionKey);
        System.out.println("流程实例ID:" + processInstance.getId());    // 流程实例ID
        System.out.println("流程定义ID:" + processInstance.getProcessDefinitionId());   // 流程定义ID
    }

    /**
     * 查询个人当前任务
     */
    @Test
    public void getMyTask() {
        String assignee = "张三";
        List<Task> taskList = processEngine.getTaskService()  // 与正在执行的任务管理的service
                .createTaskQuery()
                .taskAssignee(assignee)
                .list();
        if(taskList != null && taskList.size() > 0){
            for(Task task : taskList){
                System.out.println("任务ID：" + task.getId());
                System.out.println("任务名称：" + task.getName());
                System.out.println("任务创建时间：" + task.getCreateTime());
                System.out.println("任务办理人：" + task.getAssignee());
                System.out.println("流程实例ID：" + task.getProcessInstanceId());
                System.out.println("执行对象ID：" + task.getExecutionId());
                System.out.println("流程定义ID：" + task.getProcessDefinitionId());
            }
        }
    }

    /**
     * 完成我的任务
     */
    @Test
    public void completeMyTask(){
        String taskId = "5004";
        processEngine.getTaskService()  // 与正在执行的任务管理的service
            .complete(taskId);
        System.out.println("完成任务！任务ID：" + taskId);
    }
}
