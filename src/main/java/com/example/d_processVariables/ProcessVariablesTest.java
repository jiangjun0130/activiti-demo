package com.example.d_processVariables;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipInputStream;

public class ProcessVariablesTest {

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();


    /**
     * 部署流程定义（从zip）
     */
    @Test
    public void deployProcessDefinition_zip() {
        InputStream inputStreamBpmn = this.getClass().getResourceAsStream("/diagrams/processVariable.bpmn");
        InputStream inputStreamPng = this.getClass().getResourceAsStream("/diagrams/processVariable.png");
        Deployment deploy = processEngine.getRepositoryService()    // 与流程定义和部署相关的service
                .createDeployment() // 创建部署对象
                .name("流程变量")   // 添加部署名称
                //.addZipInputStream(new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("diagrams/helloworld.zip")))
                .addInputStream("processVariable.bpmn", inputStreamBpmn)
                .addInputStream("processVariable.png", inputStreamPng)
                .deploy();

        System.out.println("部署ID：" + deploy.getId());
        System.out.println("部署名称：" + deploy.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstance() {
        String processDefinitionKey = "processVariables";
        ProcessInstance processInstance = processEngine.getRuntimeService()   // 与正在执行的流程实例和执行对象相关的service
                // 使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中Id属性
                // 使用key启动，默认使用最新版本的流程启动
                .startProcessInstanceByKey(processDefinitionKey);
        System.out.println("流程实例ID:" + processInstance.getId());    // 流程实例ID
        System.out.println("流程定义ID:" + processInstance.getProcessDefinitionId());   // 流程定义ID
    }

    /**
     * 设置流程变量
     */
    @Test
    public void setVariables(){
        /** 与任务（正在执行） */
        TaskService taskService = processEngine.getTaskService();
        String taskId = "40004";
        /** 一: 设置流程变量， 使用基本数据类型 */
//        taskService.setVariableLocal(taskId, "请假天数", 5);
//        taskService.setVariable(taskId, "请假日期", new Date());
//        taskService.setVariable(taskId, "请假原因", "不想干，出去走走");

        /** 二：设置流程变量，使用javabean*/
        /**
         * 当一个javabean（实现序列号）并设置到流程变量中，要去javabean的属性不能再发生变化。
         * 如果发生变化，在获取的时候，将会抛出异常
         *
         * 解决方案：
         *  在Person类中添加：serialVersionUID。固定序列号版本号
         *
         */
        Person person = new Person();
        person.setId(1);
        person.setName("翠花");
        taskService.setVariable(taskId, "人员信息（添加固定版本）", person);
    }

    /**
     * 获取流程变量
     */
    @Test
    public void getVariables(){
        TaskService taskService = processEngine.getTaskService();
        String taskId = "40004";
        /** 一: 获取流程变量， 使用基本数据类型 */

//        Integer days = (Integer) taskService.getVariable(taskId, "请假天数");
//        Date date = (Date) taskService.getVariable(taskId, "请假日期");
//        String reason = (String) taskService.getVariable(taskId, "请假原因");
//
//        System.out.println("请假天数:" + days);
//        System.out.println("请假日期:" + date);
//        System.out.println("请假原因:" + reason);
        /** 二：获取流程变量，使用javabean*/
        Person person = (Person) taskService.getVariable(taskId,"人员信息（添加固定版本）");
        System.out.println(person.getId() + "-" + person.getName());
    }

    /**
     * 模拟设置和获取流程变量的场景
     */
    public void setAndGetVariables(){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();

        // 设置流程变量
//        runtimeService.setVariable(executionId, variableName, value);
//        runtimeService.setVariables(executionId, variables);
//
//        taskService.setVariable(taskId, variableName, value);
//        taskService.setVariables(taskId, variables);

        // 获取流程变量
//        runtimeService.getVariable(executionId, variableName);
//        runtimeService.getVariables(executionId);
//        runtimeService.getVariables(executionId, variableNames);


    }

    /**
     * 完成我的任务
     */
    @Test
    public void completeMyTask(){
        String taskId = "47502";
        processEngine.getTaskService()  // 与正在执行的任务管理的service
                .complete(taskId);
        System.out.println("完成任务！任务ID：" + taskId);
    }

    /**
     * 查询流程变量历史表
     */
    @Test
    public void findHistoryProcessVariables(){
        List<HistoricVariableInstance> list = processEngine.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .variableName("请假天数")
                .list();
        if(list != null && list.size() > 0){
            for(HistoricVariableInstance var : list){
                System.out.println(var.getId() + "  " + var.getProcessInstanceId() + "  " + var.getVariableTypeName() + "   " + var.getValue());
                System.out.println("###########################");
            }
        }

    }
}
