package com.example.e_historyQuery;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.junit.Test;

import java.util.List;

public class HistoryQueryTest {

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

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

    /**
     * 查询历史活动
     */
    @Test
    public void findHistoryActiviti(){
        String processInstanceId = "40001";
        List<HistoricActivityInstance> list = processEngine.getHistoryService()
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
        if(list != null && list.size() > 0){
            for(HistoricActivityInstance instance : list){
                System.out.println(instance.getId() + " " + instance.getActivityType() + "  " + instance.getStartTime() + " " + instance.getEndTime() + "   " + instance.getDurationInMillis() + "  " + instance.getActivityName());
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            }
        }
    }

    /**
     * 查询历史任务
     */
    @Test
    public void findHistoryTask(){
        String processInstanceId = "40001";
        List<HistoricTaskInstance> list = processEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime().asc()
                .list();
        if(list != null && list.size() > 0){
            for(HistoricTaskInstance instance : list){
                System.out.println(instance.getId() + " " + instance.getName() + "  " + "   " + instance.getProcessInstanceId() + " " + instance.getStartTime() + " " + instance.getEndTime() + "   " + instance.getDurationInMillis());
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            }
        }
    }

    /**
     * 查询历史流程变量
     */
    @Test
    public void findHistoryProcessVariables(){
        String processInstanceId = "40001";
        List<HistoricVariableInstance> list = processEngine.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        if(list != null && list.size() > 0){
            for(HistoricVariableInstance instance : list){
                System.out.println(instance.getId() + " " + instance.getVariableName() + "  " + "   " + instance.getProcessInstanceId() + " " + instance.getVariableTypeName() + " " + instance.getValue());
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            }
        }
    }

}
