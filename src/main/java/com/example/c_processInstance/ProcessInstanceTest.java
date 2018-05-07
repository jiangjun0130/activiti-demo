package com.example.c_processInstance;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.List;
import java.util.zip.ZipInputStream;

public class ProcessInstanceTest {

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();


    /**
     * 部署流程定义（从zip）
     */
    @Test
    public void deployProcessDefinition_zip() {
        Deployment deploy = processEngine.getRepositoryService()    // 与流程定义和部署相关的service
                .createDeployment() // 创建部署对象
                .name("流程定义zip")   // 添加部署名称
                .addZipInputStream(new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("diagrams/helloworld.zip")))
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
        String assignee = "李四";
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
     * 完成我的任务
     */
    @Test
    public void completeMyTask(){
        String taskId = "17504";
        processEngine.getTaskService()  // 与正在执行的任务管理的service
                .complete(taskId);
        System.out.println("完成任务！任务ID：" + taskId);
    }

    /**
     *  查询流程状态
     */
    @Test
    public void isProcessEnd(){
        String processInstanceId = "17501";
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if(processInstance == null){
            System.out.println("流程已结束！");
        }else{
            System.out.println("流程没有结束！");
        }
    }

    /**
     * 查询历史任务
     */
    @Test
    public void findHistoryTask(){
        String taskAssignee = "张三";
        List<HistoricTaskInstance> list = processEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .taskAssignee(taskAssignee)
                .list();
        if(list != null && list.size() > 0){
            for(HistoricTaskInstance instance : list){
                System.out.println("task id:" + instance.getId());
                System.out.println("task name:" + instance.getName());
                System.out.println("process instance id:" + instance.getProcessInstanceId());
                System.out.println("start  time:" + instance.getStartTime());
                System.out.println("end  time:" + instance.getEndTime());
                System.out.println("task duration:" + instance.getDurationInMillis());
                System.out.println("#################################");
            }
        }
    }

    /**
     * 查询历史流程实例
     */
    @Test
    public void findHistoryProcessInstance(){
        HistoricProcessInstance instance = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processInstanceId("17501")
                .singleResult();
        System.out.println(instance.getId() + " " + instance.getProcessDefinitionId() + "   " + instance.getStartTime() + " " + instance.getEndTime() + "   " + instance.getDurationInMillis());
    }
}

