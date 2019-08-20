package com.atguigu.atcrowdfunding.user.controller;

import com.atguigu.front.common.AppResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户实名审核模块")
@RestController
@RequestMapping("/user/auth")
public class UserRealAuthController {

    @ApiOperation("认证申请第1步-用户认证申请开始")
    @GetMapping("/start")
    public AppResponse<String> startAuth(){
        return null;
    }

    @ApiOperation("认证申请第2步-提交基本信息")
    @PostMapping("/baseinfo")
    public AppResponse<String> submitBaseInfo(){
        return null;
    }

    @ApiOperation("认证申请第3步-上传资质信息")
    @PostMapping("/certs")
    public AppResponse<String> submitCerts(){
        return null;
    }

    @ApiOperation("认证申请第4步-确认邮箱信息")
    @PostMapping("/email")
    public AppResponse<String> confirmEmail(){
        return null;
    }

    @ApiOperation("认证申请第5步-提交实名认证申请")
    @PostMapping("/submit")
    public AppResponse<String> submitAuth(){
        return null;
    }

    @ApiOperation("获取需要上传的资质信息")
    @GetMapping("/certs2upload")
    public AppResponse<String> getCerts4Update(){
        return null;
    }
}
