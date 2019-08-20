package com.atguigu.project.controller;

import com.atguigu.front.common.AppResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(tags = "项目操作模块")
@RequestMapping("/project/operation")
@RestController
public class ProjectOperationController {

    @ApiOperation("项目删除")
    @DeleteMapping("/delete")
    public AppResponse<String> delete(){
        return null;
    }

    @ApiOperation("项目预览")
    @GetMapping("/preshow")
    public AppResponse<String> preshow(){
        return null;
    }

    @ApiOperation("项目问题查看")
    @GetMapping("/question")
    public AppResponse<String> question(){
        return null;
    }

    @ApiOperation("项目问题添加")
    @PostMapping("/question")
    public AppResponse<String> addQuestion(){
        return null;
    }

    @ApiOperation("项目问题答案添加")
    @PostMapping("/question/answer")
    public AppResponse<String> addQuestionAnswer(){
        return null;
    }

    @ApiOperation("项目关注")
    @PostMapping("/star")
    public AppResponse<String> star(){
        return null;
    }

    @ApiOperation("项目取消关注")
    @DeleteMapping("/star")
    public AppResponse<String> deletestar(){
        return null;
    }

    @ApiOperation("项目修改")
    @PutMapping("/update")
    public AppResponse<String> update(){
        return null;
    }
}

