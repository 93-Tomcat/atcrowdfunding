package com.atguigu.front.vo.resp;

import com.atguigu.front.bean.TMember;
import com.atguigu.front.bean.TProject;
import com.atguigu.front.bean.TReturn;
import lombok.Data;

import java.util.List;

@Data
public class ProjectAllAllInfoVo extends TProject {

    private String headerImage;//头图
    private List<String> detailImages;//详情图
    private String memberName;//发布者的名字
    private List<TReturn> returns;//所有的档位信息
    private Integer timeLife;//剩余时间
}
