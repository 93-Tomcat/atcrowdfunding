package com.atguigu.project.controller;

import com.atguigu.front.bean.*;
import com.atguigu.front.common.AppResponse;
import com.atguigu.front.vo.resp.ProjectAllAllInfoVo;
import com.atguigu.front.vo.resp.ProjectAllInfoVo;
import com.atguigu.project.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "项目信息模块")
@RequestMapping("/project")
@RestController
public class ProjectInfoController {


    @Autowired
    ProjectService projectService;

    @ApiOperation("获取首页广告项目")
    @GetMapping("/adv")
    public AppResponse<String> getIndexAdv() {
        return null;
    }

    @ApiOperation("获取项目总览列表")
    @GetMapping("/all/index")
    public AppResponse<List<ProjectAllInfoVo>> getAllIndex() {

        List<ProjectAllInfoVo> projects = projectService.getAllProjectsInfos();
        return AppResponse.success(projects);
    }

    @ApiOperation("获取项目详情")
    @GetMapping("/info/detail/{id}")
    public AppResponse<ProjectAllAllInfoVo> getDetail(@PathVariable("id") Integer id){

        ProjectAllAllInfoVo allInfoVo = new ProjectAllAllInfoVo();

        //1、查询项目的基本信息
        TProject project = projectService.getProjectInfo(id);
        BeanUtils.copyProperties(project,allInfoVo);

        //2、查出头图地址
        TProjectImages projectImages = projectService.getProjectHeaderImage(id);
        allInfoVo.setHeaderImage(projectImages.getImgurl());
        //3、查出项目的详情图
        List<TProjectImages> detailImages =  projectService.getProjectDetailImage(id);
        List<String> images = new ArrayList<>();
        for (TProjectImages detailImage : detailImages) {
            images.add(detailImage.getImgurl());
        }
        allInfoVo.setDetailImages(images);

        //4、查询项目的所有档位信息
        List<TReturn> returns = projectService.getProjectAllReturns(id);
        allInfoVo.setReturns(returns);

        return AppResponse.success(allInfoVo);
    }

    @ApiOperation("获取首页热门推荐")
    @GetMapping("/recommend/index")
    public AppResponse<String> recommendIndex(){
        return null;
    }

    @ApiOperation("获取首页分类推荐")
    @GetMapping("/recommend/type")
    public AppResponse<String> recommendType(){
        return null;
    }

    @ApiOperation("获取项目回报档位信息")
    @GetMapping("/return/info")
    public AppResponse<String> returnInfo(){
        return null;
    }



    @ApiOperation("获取项目系统标签信息")
    @GetMapping("/sys/tags")
    public AppResponse<List<TTag>> sysTags(){


        List<TTag> tags =  projectService.getSysTags();
        return AppResponse.success(tags);
    }

    @ApiOperation("获取项目系统分类信息")
    @GetMapping("/sys/type")
    public AppResponse<List<TType>> sysType(){

        List<TType> types =  projectService.getSysTypes();
        return AppResponse.success(types);
    }
}
