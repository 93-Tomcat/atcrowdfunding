package com.atguigu.atcrowdfunding.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 以后要定制SpringMVC我们可以写一个类实现WebMvcConfigurer接口；
 * 1）、定制addResourceHandlers；资源映射规则
 * 2）、定制视图解析器规则
 * 3）、定制添加拦截器等
 */
@Configuration
public class AppMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**.html").addResourceLocations("classpath:/public/");
    }


    /**
     * 穿越火线
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        /*    <!--  @GetMapping("/login.html")
    public String loginPage(){


        return "login";
    }-->
    <mvc:view-controller path="/login.html" view-name="login"/>*/
        //发送了/login.html,视图解析拼串来到指定地址  templates/login.html
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
        registry.addViewController("/index.html").setViewName("index");


        registry.addViewController("/start-step-2.html").setViewName("protected/project/start-step-2");
        registry.addViewController("/start-step-3.html").setViewName("protected/project/start-step-3");
    }
}
