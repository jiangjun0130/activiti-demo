package com.example.b_processDefinition;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

public class ProcessDefinitionTest {

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    /**
     * 部署流程定义（从classpath）
     */
    @Test
    public void deployProcessDefinition_classpath() {
        Deployment deploy = processEngine.getRepositoryService()    // 与流程定义和部署相关的service
                .createDeployment() // 创建部署对象
                .name("流程定义")   // 添加部署名称
                .addClasspathResource("diagrams/helloworld.bpmn") // 从classpath资源加载，一次只能加载一个文件
                .addClasspathResource("diagrams/helloworld.png") // 从classpath资源加载，一次只能加载一个文件
                .deploy();

        System.out.println("部署ID：" + deploy.getId());
        System.out.println("部署名称：" + deploy.getName());
    }

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
     * 查询流程定义
     */
    @Test
    public void findProcessDefinition() {
        List<ProcessDefinition> list = processEngine.getRepositoryService()    // 与流程定义和部署相关的service
                .createProcessDefinitionQuery()     // 创建流程定义查询
                // 查询条件
                //.deploymentId() // 使用部署id查询
                //.processDefinitionId()  // 使用流程定义id查询
                //.processDefinitionKey() // 使用流程定义的key查询
                //.processDefinitionNameLike()    // 使用流程定义的名称模糊查询
                /** 排序 */
                .orderByProcessDefinitionVersion().asc()    // 按照版本升序
                //.orderByProcessDefinitionName().desc()  // 按照流程定义的名称降序
                /** 返回结果及*/
                .list();// 返回集合列表
                //.singleResult() // 返回唯一结果
                //.count()    // 结果数量
                //.listPage() // 分页查询

        if(list != null && list.size() > 0){
            for(ProcessDefinition pd : list){
                System.out.println("流程定义id：" + pd.getId());
                System.out.println("流程定义名称：" + pd.getName());
                System.out.println("流程定义key：" + pd.getKey());
                System.out.println("流程定义版本：" + pd.getVersion());
                System.out.println("资源文件bpmn文件：" + pd.getResourceName());
                System.out.println("资源名称png文件：" + pd.getDiagramResourceName());
                System.out.println("部署对象id：" + pd.getDeploymentId());
                System.out.println("################################################ ");
            }
        }
    }

    /**
     * 删除流程定义
     */
    @Test
    public void deleteProcessDefinition(){
        String deploymentId = "1";
        /*processEngine.getRepositoryService()
                // 不带级联删除。只能删除没有启动的流程，如果流程已启动就会抛出异常
                .deleteDeployment(deploymentId);    // 使用部署id删除
*/
        /**
         * 级联删除，不管流程是否启动，都可以删除
         */
        processEngine.getRepositoryService()
            .deleteDeployment(deploymentId, true);

        System.out.println("删除成功！");
    }

    /**
     * 查看流程图
     */
    @Test
    public void viewPic() throws IOException {
        String deploymentId = "12501";

        // 获取图片资源名称
        List<String> resourceNames = processEngine.getRepositoryService()
                .getDeploymentResourceNames(deploymentId);
        String resourceName = "";
        if(resourceNames != null && resourceNames.size() > 0){
            for(String name : resourceNames){
                if(name.indexOf(".png") > 0){
                    resourceName = name;
                }
            }
        }

        InputStream inputStream = processEngine.getRepositoryService()
                .getResourceAsStream(deploymentId, resourceName);
        File file = new File("/Users/Apple/Desktop/" + resourceName);
        FileUtils.copyInputStreamToFile(inputStream, file);
    }

    /**
     * 查询最新版本流程定义
     */
    @Test
    public void findLastVersionProcessDefinition(){
        List<ProcessDefinition> list = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion().asc()
                .list();
        Map<String, ProcessDefinition> map = new LinkedHashMap<>();
        if(list != null && list.size() > 0){
            for(ProcessDefinition pd : list){
                map.put(pd.getKey(), pd);
            }
        }
        List<ProcessDefinition> pdList = new ArrayList<>(map.values());
        if(pdList != null && pdList.size() > 0){
            for(ProcessDefinition pd : pdList){
                System.out.println("流程定义id：" + pd.getId());
                System.out.println("流程定义名称：" + pd.getName());
                System.out.println("流程定义key：" + pd.getKey());
                System.out.println("流程定义版本：" + pd.getVersion());
                System.out.println("资源文件bpmn文件：" + pd.getResourceName());
                System.out.println("资源名称png文件：" + pd.getDiagramResourceName());
                System.out.println("部署对象id：" + pd.getDeploymentId());
                System.out.println("################################################ ");
            }
        }
    }

    /**
     * 删除流程定义
     */
    @Test
    public void deleteProcessDefinitionByKey(){
        String processDefinitionKey = "helloworld";
        // 根据key查询所有流程定义，查询出所有版本
        List<ProcessDefinition> list = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .list();
        // 获取每个流程定义的id
        if(list != null && list.size() > 0){
            for(ProcessDefinition pd : list){
                String deploymentId = pd.getDeploymentId();
                processEngine.getRepositoryService()
                        .deleteDeployment(deploymentId, true);
            }
        }
    }

}
