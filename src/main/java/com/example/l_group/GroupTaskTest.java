package com.example.l_group;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupTaskTest {

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();


    /**
     * 部署流程定义（从zip）
     */
    @Test
    public void deployProcessDefinition_inputStream() {
        InputStream inputStreamBpmn = this.getClass().getResourceAsStream("groupTask.bpmn");
        InputStream inputStreamPng = this.getClass().getResourceAsStream("groupTask.png");
        Deployment deploy = processEngine.getRepositoryService()    // 与流程定义和部署相关的service
                .createDeployment() // 创建部署对象
                .name("组任务")   // 添加部署名称
                //.addZipInputStream(new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("diagrams/helloworld.zip")))
                .addInputStream("groupTask.bpmn", inputStreamBpmn)
                .addInputStream("groupTask.png", inputStreamPng)
                .deploy();

        System.out.println("部署ID：" + deploy.getId());
        System.out.println("部署名称：" + deploy.getName());
    }

    /**
     * 启动流程实例+使用流程变量指定组任务成员
     */
    @Test
    public void startProcessInstance() {
        String processDefinitionKey = "groupTask";
        // 启动刘实例同时，设置流程变量。使用流程变量指定组任务成员
        Map<String, Object> variables = new HashMap<>();
        variables.put("userIds", "a,b,c,d");
        ProcessInstance processInstance = processEngine.getRuntimeService()   // 与正在执行的流程实例和执行对象相关的service
                // 使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中Id 属性
                // 使用key启动，默认使用最新版本的流程启动
                .startProcessInstanceByKey(processDefinitionKey, variables);

        System.out.println("流程实例ID:" + processInstance.getId());    // 流程实例ID
        System.out.println("流程定义ID:" + processInstance.getProcessDefinitionId());   // 流程定义ID

    }

    /**
     * 查询个人当前任务
     */
    @Test
    public void getPersonalTask() {
        String assignee = "B";
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
     * 查询当前组任务
     */
    @Test
    public void getGroupTask() {
        String candidateUser = "a";
        List<Task> taskList = processEngine.getTaskService()  // 与正在执行的任务管理的service
                .createTaskQuery()
                /** 查询条件 */
                //.taskAssignee(a ssignee) // 查看 个人任务，指定办理人
                .taskCandidateUser(candidateUser)    // 组任务的办理人
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
     * 查询正在执行的任务办理人表
     */
    @Test
    public void findRunPersonTask(){
        String taskId = "255004";
        List<IdentityLink> links = processEngine.getTaskService()
                .getIdentityLinksForTask(taskId);
        if(links != null && links.size() > 0){
            for(IdentityLink link : links){
                System.out.println(link.getTaskId() + " " + link.getType() + "  " + link.getProcessInstanceId() + " " + link.getUserId());
            }
        }
    }

    /**
     * 查询历史任务的办理人表
     */
    @Test
    public void findHisPersonTask(){
        String processInstanceId = "255001";
        List<HistoricIdentityLink> links1 = processEngine.getHistoryService()
                // 根据processInstanceId查询获取到参与者
                .getHistoricIdentityLinksForProcessInstance(processInstanceId);
        if(links1 != null && links1.size() > 0){
            System.out.println("############参与者###########");
            for(HistoricIdentityLink link : links1){
                System.out.println(link.getTaskId() + " " + link.getType() + "  " + link.getProcessInstanceId() + " " + link.getUserId());
            }
        }
        String taskId = "255004";
        List<HistoricIdentityLink> links2 = processEngine.getHistoryService()
                // 根据ptaskId查询获取到候选者
                .getHistoricIdentityLinksForTask(taskId);
        if(links2 != null && links2.size() > 0){
            System.out.println("############候选者###########");
            for(HistoricIdentityLink link : links2){
                System.out.println(link.getTaskId() + " " + link.getType() + "  " + link.getProcessInstanceId() + " " + link.getUserId());
            }
        }
    }

    /**
     * 拾取任务，将组任务分给个人任务
     * 指定任务的办理人
     */
    @Test
    public void claim(){
        // 将组任务分配给个人任务(可以使组任务中的成员，也可以是非组任务中的成员)
        String taskId = "275005";
        String userId = "b";
        processEngine.getTaskService()
                .claim(taskId, userId);
    }

    /**
     * 将个人任务回退到组任务
     * 前提：之前一定是一个组任务
     */
    @Test
    public void setAssignee(){
        String taskId = "255004";
        processEngine.getTaskService()
                .setAssignee(taskId, null);
    }

    /**
     * 向组任务中添加成员
     */
    @Test
    public void addGroupUser(){
        String taskId = "255004";
        String userId = "E";
        processEngine.getTaskService()
                .addCandidateUser(taskId, userId);
    }

    /**
     * 向组任务中删除成员
     */
    @Test
    public void deleteGroupUser(){
        String taskId = "255004";
        String userId = "B";
        processEngine.getTaskService()
                .deleteCandidateUser(taskId, userId);
    }

    /**
     * 完成任务的同时，设置流程变量
     */
    @Test
    public void completeMyTask(){
        String taskId = "275005";
        // 完成任务的同时，设置流程变量 。使用流程变量来指定完成 任务后，下一个连线对于sequenceFlow.bpmn中的表达式${message=='不重要'}

        processEngine.getTaskService()  // 与正在执行的任务管理的service
                .complete(taskId);
        System.out.println("完成任务！任务ID：" + taskId);
    }
}
