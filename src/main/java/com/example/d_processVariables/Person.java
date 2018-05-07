package com.example.d_processVariables;

import java.io.Serializable;

public class Person implements Serializable {

    /**
     * 固定序列号版本
     * 修改类属性后，获取流程变量不再报错
     */
    private static final long serialVersionUID = 213044983961597728L;

    private Integer id;

    private String name;

    private String education;

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
