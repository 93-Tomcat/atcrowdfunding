package com.atguigu.atcrowdfunding.user.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.atcrowdfunding.user.service.MemberService;
import com.atguigu.front.bean.TMember;
import com.atguigu.front.bean.TMemberAddress;
import com.atguigu.front.common.AppResponse;
import com.atguigu.front.constant.AppConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Api(tags = "用户个人信息模块")
@RestController
@RequestMapping("/user/info")
public class UserInfoController {

    @Autowired
    MemberService memberService;

    @Autowired
    StringRedisTemplate redisTemplate;
    @ApiOperation("获取个人信息")
    @GetMapping("/")
    public AppResponse<TMember> getUserInfo(@RequestParam("accessToken") String accessToken){

        //续期？每一个操作过来，重新设置login:member的超时

        TMember member = memberService.getMemberInfo(accessToken);
        member.setId(null);
        member.setUserpswd(null);

        //redisTemplate.expire(AppConstant.MEMBER_LOGIN_CACHE_PREFIX+accessToken,30, TimeUnit.MINUTES);

        return AppResponse.success(member);
    }

    @ApiOperation("更新个人信息")
    @PostMapping("/")
    public AppResponse<String> updateUserInfo(){
        return null;
    }

    @ApiOperation("获取用户收货地址")
    @GetMapping("/address")
    public AppResponse<List<TMemberAddress>> getUserAddress(@RequestParam("accessToken") String accessToken){
        String json = redisTemplate.opsForValue().get(AppConstant.MEMBER_LOGIN_CACHE_PREFIX + accessToken);
        TMember member = JSON.parseObject(json, TMember.class);

        List<TMemberAddress> addresses =   memberService.getMemberAddress(member.getId());
        return AppResponse.success(addresses);
    }

    @ApiOperation("新增用户收货地址")
    @PostMapping("/address")
    public AppResponse<TMemberAddress> addUserAddress(@RequestParam("accessToken") String accessToken,
                                              @RequestParam("address") String address){
        TMemberAddress address1 = memberService.addAddress(accessToken,address);
        return AppResponse.success(address1);
    }

    @ApiOperation("修改用户收货地址")
    @PutMapping("/address")
    public AppResponse<String> updateUserAddress(){
        return null;
    }

    @ApiOperation("删除用户收货地址")
    @DeleteMapping("/address")
    public AppResponse<String> deleteUserAddress(){
        return null;
    }

    @ApiOperation("获取我发起的项目")
    @GetMapping("/create/project")
    public AppResponse<String> getCreateProject(){
        return null;
    }

    @ApiOperation("获取我的系统消息")
    @GetMapping("/message")
    public AppResponse<String> getMyMessage(){
        return null;
    }

    @ApiOperation("查看我的订单")
    @GetMapping("/order")
    public AppResponse<String> getMyOrder(){
        return null;
    }

    @ApiOperation("删除我的订单")
    @DeleteMapping("/order")
    public AppResponse<String> deleteMyOrder(){
        return null;
    }

    @ApiOperation("获取我关注的项目")
    @GetMapping("/star/project")
    public AppResponse<String> getStarProject(){
        return null;
    }

    @ApiOperation("获取我支持的项目")
    @GetMapping("/support/project")
    public AppResponse<String> getSupportProject(){
        return null;
    }
}
