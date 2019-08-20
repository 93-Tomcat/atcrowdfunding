package com.atguigu.atcrowdfunding.user.controller;


import com.alibaba.fastjson.JSON;
import com.atguigu.atcrowdfunding.user.component.SmsTemplate;
import com.atguigu.atcrowdfunding.user.service.MemberService;
import com.atguigu.front.vo.req.MemberRegisterVo;
import com.atguigu.front.vo.resp.MemberRespsonVo;
import com.atguigu.front.bean.TMember;
import com.atguigu.front.common.AppResponse;
import com.atguigu.front.constant.AppConstant;
import com.atguigu.front.exception.UserEmailException;
import com.atguigu.front.exception.UserLoginacctException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * svn：idea各种 颜色；
 * 菊灰色：没有纳入到版本控制，忽略的文件，提交不带的文件
 * 橘红色：需要手动告诉svn这些是要忽略还是添加到svn中;
 *    右键-->add to vcs：添加到版本控制
 *
 *
 * (origins = "*",methods = {RequestMethod.GET,RequestMethod.POST,
 *         RequestMethod.OPTIONS,
 *         RequestMethod.DELETE,
 * RequestMethod.PUT})
 */
@CrossOrigin //允许跨域
@Api(tags = "用户登录、注册服务")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserLoginRegistController {

    @Autowired
    SmsTemplate smsTemplate;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MemberService memberService;

    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginacct",value = "账号（手机号）",required = true),
            @ApiImplicitParam(name = "password",value = "密码",required = true)
    })
    @PostMapping("/login")
    public AppResponse<MemberRespsonVo> login(@RequestParam(value = "loginacct",required = true) String loginacct,
                                              @RequestParam(value = "password",required = true) String password){


        TMember member = memberService.login(loginacct,password);
        if(member == null){
            return AppResponse.fail().msg("登录失败，账号密码错误");
        }else {
            //登录成功，用户信息存redis，key用的访问令牌
            MemberRespsonVo memberRespsonVo = new MemberRespsonVo();
            //TMember和MemberRespsonVo的同名属性会对拷过来
            BeanUtils.copyProperties(member,memberRespsonVo);

            String token = UUID.randomUUID().toString().replace("-", "");
            memberRespsonVo.setAccessToken(token);

            //保存登录的用户token和用户详情（转为json字符串）的对应信息到redis
            String jsonString = JSON.toJSONString(member);
            redisTemplate.opsForValue().set(AppConstant.MEMBER_LOGIN_CACHE_PREFIX+token,jsonString,30,TimeUnit.MINUTES);

            return AppResponse.success(memberRespsonVo);

        }
    }


    /**
     * Controller负责收集，顶多做一个 值合法性（非空）
     *
     * 业务逻辑合法性；（service来做）
     * @param memberRegisterVo
     * @return
     *
     * SpringCloud：Http+Json;
     *      @RequestBody:将请求体中的json数据转为指定的这对象
     *
     * 以后的post请去都代表接受json数据
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public AppResponse<String> register(MemberRegisterVo memberRegisterVo){

        log.debug("{} 用户正在注册：",memberRegisterVo.getMobile());
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        //校验；
        if(StringUtils.isEmpty(memberRegisterVo.getCode())){
            return AppResponse.fail().msg("验证必须填写");
        }
        if(StringUtils.isEmpty(memberRegisterVo.getEmail())){
            return AppResponse.fail().msg("邮箱必须填写");
        }
        if(StringUtils.isEmpty(memberRegisterVo.getMobile())){
            return AppResponse.fail().msg("手机号必须填写");
        }
        if(StringUtils.isEmpty(memberRegisterVo.getPassword())){
            return AppResponse.fail().msg("密码必须填写");
        }

        //校验验证码
        String code = redisTemplate.opsForValue().get(AppConstant.CODE_CACHE_PREFIX + memberRegisterVo.getMobile());
        if(StringUtils.isEmpty(code)){
            return AppResponse.fail().msg("验证码过期，请重新获取");
        }else {
            if(!code.equalsIgnoreCase(memberRegisterVo.getCode())){
                return AppResponse.fail().msg("验证码错误");
            }else {
                //异常机制；验证码验证通过；销毁验证码；
                redisTemplate.delete(AppConstant.CODE_CACHE_PREFIX + memberRegisterVo.getMobile());
                try {
                    memberService.regist(memberRegisterVo);
                } catch (UserEmailException e) {
                    return AppResponse.fail().msg(e.getMessage());
                } catch (UserLoginacctException e) {
                    return AppResponse.fail().msg(e.getMessage());
                }
            }
        }


        return AppResponse.success("");
    }


    //cookie/session；浏览器
    /**
     * 浏览器：
     *      同一个页面共享：pageContext
     *      同一次请求：request
     *      同一次会话：session：Map
     *      同一个应用：application；
     *
     * 多端了；
     *      同一个页面共享：各端使用自己的方式
     *      同一次请求共享数据：将数据以json写出去；
     *      同一次会话：把数据一个公共的地方【redis】，
     *                ：把数据一个公共的地方【redis】，
     */
    @ApiOperation("获取短信验证码")
    @PostMapping ("/sendsms")
    public AppResponse<String> sendSms(@RequestParam("mobile") String mobile){

        log.debug("短信验证码发送...........");
        String code = UUID.randomUUID().toString().replace("-","").substring(0,5);
        smsTemplate.sendCodeSms(mobile,code);


        //1、将验证码放在redis中，下一次进行验证； k（mobile）-v（code）；redis中的key一般都要设置过期时间
        //所有在redis中应该有前缀，每一个业务的前缀不一样；
        redisTemplate.opsForValue().set(AppConstant.CODE_CACHE_PREFIX+mobile,code,5, TimeUnit.MINUTES);

        return AppResponse.success("").msg("短信发送完成");
    }

    @ApiOperation("重置密码")
    @PostMapping("/reset")
    public AppResponse<String> resetPassword(){
        return null;
    }



}
