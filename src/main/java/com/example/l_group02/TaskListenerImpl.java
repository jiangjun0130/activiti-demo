package com.example.l_group02;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class TaskListenerImpl implements TaskListener {

    /**
     * 用来指定任务办理人
     * @param delegateTask
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        // 指定个人任务的办理人，也可以指定组任务的办理人
        // 通过类查询数据库，将下一个任务的办理人查询获取，通过setAssign()指定办理人
        delegateTask.addCandidateUser("Tom");
        delegateTask.addCandidateUser("Cook");
    }

}
