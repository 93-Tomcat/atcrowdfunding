package com.atguigu.front.bean;

import java.util.ArrayList;
import java.util.List;

public class TTag {
    private Integer id;

    private Integer pid;

    private String name;

    private List<TTag> childrens = new ArrayList<>();

    public List<TTag> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<TTag> childrens) {
        this.childrens = childrens;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}