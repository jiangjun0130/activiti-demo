package com.example.f_sequenceFlow;

import com.example.d_processVariables.Person;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequenceFlowTest {

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();


    /**
     * 部署流程定义（从zip）
     */
    @Test
    public void deployProcessDefinition_inputStream() {
        InputStream inputStreamBpmn = this.getClass().getResourceAsStream("sequenceFlow.bpmn");
        InputStream inputStreamPng = this.getClass().getResourceAsStream("sequenceFlow.png");
        Deployment deploy = processEngine.getRepositoryService()    // 与流程定义和部署相关的service
                .createDeployment() // 创建部署对象
                .name("连线")   // 添加部署名称
                //.addZipInputStream(new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("diagrams/helloworld.zip")))
                .addInputStream("sequenceFlow.bpmn", inputStreamBpmn)
                .addInputStream("sequenceFlow.png", inputStreamPng)
                .deploy();

        System.out.println("部署ID：" + deploy.getId());
        System.out.println("部署名称：" + deploy.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstance() {
        String processDefinitionKey = "sequenceFlow";
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
                /** 查询条件 */
                .taskAssignee(assignee) // 查看 个人任务，指定办理人
                //.taskCandidateUser()    // 组任务的办理人
                //.processDefinitionId()  // 使用流程定义id查询
                //.processInstanceId()    // 使用流程实例id查询
                //.executionId()    // 执行对象id查询
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
     * 完成任务的同时，设置流程变量
     */
    @Test
    public void completeMyTask(){
        String taskId = "62503";
        // 完成任务的同时，设置流程变量 。使用流程变量来指定完成 任务后，下一个连线对于sequenceFlow.bpmn中的表达式${message=='不重要'}
        Map<String, Object> variables = new HashMap<>();
        variables.put("message", "重要");
        processEngine.getTaskService()  // 与正在执行的任务管理的service
                .complete(taskId, variables);
        System.out.println("完成任务！任务ID：" + taskId);
    }

}
