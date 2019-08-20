package com.atguigu.atcrowdfunding.app.controller;

import com.atguigu.atcrowdfunding.app.service.feign.UserServiceFeign;
import com.atguigu.front.bean.TMemberAddress;
import com.atguigu.front.common.AppResponse;
import com.atguigu.front.vo.req.MemberRegisterVo;
import com.atguigu.front.vo.resp.MemberRespsonVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceFeign userService;
    @PostMapping("/login")
    public String doLogin(String loginacct, String password, HttpSession session, Model model, RedirectAttributes redirectAttributes){

        AppResponse<MemberRespsonVo> login = userService.login(loginacct, password);
        if(login.getCode() == 0){
            //登录成功
            MemberRespsonVo data = login.getData();
            session.setAttribute("loginUser",data);


            //回到首页
            return "redirect:/index.html";
        }else {

            model.addAttribute("msg",login.getMsg());
            model.addAttribute("loginacct",loginacct);
            //回到登录页
            return "login";
        }


    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/index.html";
    }

    //重定向携带数据；RedirectAttributes redirectAttributes
    @PostMapping("/reg")
    public String registe(MemberRegisterVo vo,RedirectAttributes redirectAttributes){

        //cloud会把vo转为json，导致远程没有写@RequestBody的服务就将json转不成对象，
        /**
         * 导致原因？
         *      发出去用json。接受没说用json接；
         *
         * 最终解决的两种方式：
         *      1）、发出去用json。接受用json接；远程服务加一个@RequestBody；
         *
         *      2）、发出不用json发（k=v）,接受没用json接(用k=v)；
         *
         */
        AppResponse<String> register = userService.register(vo);
        if(register.getCode() == 0){
            //注册成功 //成去登录页面；
            //提示注册成功可以登录
            redirectAttributes.addFlashAttribute("msg","注册成功可以登录了");

            //解决表单重复提交；最简单暴力的方式就是重定向；
            return "redirect:/login.html";
        }else {
            redirectAttributes.addFlashAttribute("msg",register.getMsg());
            redirectAttributes.addFlashAttribute("vo",vo);
            return "redirect:/reg.html";
        }


    }

    @ResponseBody
    @PostMapping("/address")
    public AppResponse<TMemberAddress> addAddress(@RequestParam("address") String address, HttpSession session){
        MemberRespsonVo loginUser = (MemberRespsonVo) session.getAttribute("loginUser");
        AppResponse<TMemberAddress> response = userService.addUserAddress(loginUser.getAccessToken(), address);
        return response;
    }


}
