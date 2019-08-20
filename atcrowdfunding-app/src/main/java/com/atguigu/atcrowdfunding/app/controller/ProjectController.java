package com.atguigu.atcrowdfunding.app.controller;

import com.atguigu.atcrowdfunding.app.component.OssTemplate;
import com.atguigu.atcrowdfunding.app.service.feign.ProjectFeignService;
import com.atguigu.front.bean.TTag;
import com.atguigu.front.bean.TType;
import com.atguigu.front.common.AppResponse;
import com.atguigu.front.vo.req.BaseVo;
import com.atguigu.front.vo.req.ProjectBaseInfoVo;
import com.atguigu.front.vo.req.ProjectReturnVo;
import com.atguigu.front.vo.resp.MemberRespsonVo;
import com.atguigu.front.vo.resp.ProjectAllAllInfoVo;
import com.atguigu.front.vo.resp.ProjectTempVo;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    ProjectFeignService projectFeignService;

    @Autowired
    OssTemplate ossTemplate;

    private  int differentDayMillisecond (Date preTime,Date lastTime)
    {


        int day = (int)((lastTime.getTime()-preTime.getTime())/(3600*1000*24));
        return day;
    }


    @GetMapping("/info/{id}")
    public String projectInfo(@PathVariable("id") Integer id,Model model) throws ParseException {
        //1、调用atcrowdfunding-project查询项目的详细信息
        AppResponse<ProjectAllAllInfoVo> detail = projectFeignService.getDetail(id);
        if(detail.getCode() == 0){
            ProjectAllAllInfoVo data = detail.getData();

            String createdate = data.getCreatedate();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parse = format.parse(createdate);
            Integer day = data.getDay();
            Date current = new Date();
            int i = differentDayMillisecond(parse, current);
            //20
            data.setTimeLife(day-i);

            model.addAttribute("details",data);
        }
        return "protected/project/project";
    }



    /**
     * 第0步：发起众筹跳到，阅读并同意协议
     * @return
     */
    @GetMapping("/start.html")
    public String startProject(){


        return "protected/project/start";
    }

    /**
     * 第1步：阅读并同意协议跳到项目基本信息录入页面
     */
    @GetMapping("/start-step-1.html")
    public String startstep1(HttpSession session, Model model){
        //展示step1页面之前
        //1、获取项目令牌;
        MemberRespsonVo loginUser = (MemberRespsonVo) session.getAttribute("loginUser");
        AppResponse<ProjectTempVo> response = projectFeignService.init(loginUser.getAccessToken());
        ProjectTempVo data = response.getData();
        session.setAttribute("projectToken",data.getProjectToken());

        //2、远程查询分类信息
        AppResponse<List<TType>> listAppResponse = projectFeignService.sysType();
        //3、远程查询标签信息
        AppResponse<List<TTag>> listAppResponse1 = projectFeignService.sysTags();

        List<TTag> alltags = listAppResponse1.getData();
        //整理父子关系
        List<TTag> tags = new ArrayList<>();
        for (TTag tTag : alltags) {
            //遍历所有标签取出父标签
            if(tTag.getPid() == 0){
                tags.add(tTag);
            }
        }

        for (TTag tag : tags) {
            //为每一个父标签找子标签
            for (TTag tTag : alltags) {
                if(tTag.getPid() == tag.getId()){
                    tag.getChildrens().add(tTag);
                }
            }
        }

        model.addAttribute("sysTypes",listAppResponse.getData());
        model.addAttribute("sysTags",tags);
        return "/protected/project/start-step-1";
    }

    /**
     * 第2步：项目基本信息录入页面点击下一步，来到回报设置页
     */
    @PostMapping("/start-step-2.html")
    public String startstep2(ProjectBaseInfoVo projectBaseInfoVo,
                             @RequestParam("header") MultipartFile file,
                             @RequestParam("details") MultipartFile[] files,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) throws IOException {
        //收集页面的数据
        log.debug("收集到的页面的数据...{}",projectBaseInfoVo);
        MemberRespsonVo loginUser = (MemberRespsonVo) session.getAttribute("loginUser");

        //上传头图
        if(!file.isEmpty()){
            log.debug("头图{}文件上传完成",file.getOriginalFilename());
            String url = ossTemplate.upload(file.getBytes(), file.getOriginalFilename());
            projectBaseInfoVo.setHeaderImage(url);
        }

        List<String> details = new ArrayList<>();
        //上传详情图
        if(files!=null){
            for (MultipartFile multipartFile : files) {
                if(!multipartFile.isEmpty()){
                    log.debug("详情{}文件上传完成",multipartFile.getOriginalFilename());
                    String url = ossTemplate.upload(multipartFile.getBytes(), multipartFile.getOriginalFilename());
                    details.add(url);
                }
            }
        }
        projectBaseInfoVo.setDetailsImage(details);


        //把这两个令牌封装好，这些之前都在session中
        projectBaseInfoVo.setAccessToken(loginUser.getAccessToken());
        projectBaseInfoVo.setProjectToken((String) session.getAttribute("projectToken"));


        log.debug("准备好的项目所有信息的vo数据：{}，这些数据将发送出去",projectBaseInfoVo);
        //远程服务保存项目信息
        AppResponse<String> response = projectFeignService.saveBaseInfo(projectBaseInfoVo);
        if(response.getCode() == 0){
            return "redirect:/start-step-2.html";
        }else {
            //失败回到之前页面并进行回显
            redirectAttributes.addFlashAttribute("vo",projectBaseInfoVo);
            redirectAttributes.addFlashAttribute("msg","服务异常，请重新录入");
            return "redirect:/project/start-step-1.html";
        }


    }

    /**
     * 第3步：回报设置页点击下一步，来到发起人信息录入页
     */
    @ResponseBody
    @PostMapping("/start-step-3.html")
    public AppResponse<String> startstep3(@RequestBody List<ProjectReturnVo> returns,HttpSession session){
//
        MemberRespsonVo loginUser = (MemberRespsonVo) session.getAttribute("loginUser");
//        @ApiModelProperty(value = "访问令牌",required = true)
//        private String accessToken;//访问令牌
//        @ApiModelProperty(value = "项目临时令牌",required = true)
//        private String projectToken;//项目的临时token；项目的唯一标识
        for (ProjectReturnVo returnVo : returns) {
            returnVo.setAccessToken(loginUser.getAccessToken());
            returnVo.setProjectToken((String) session.getAttribute("projectToken"));
        }

        log.debug("收到的页面提交来的数据：{}",returns);
        AppResponse<String> appResponse = projectFeignService.addReturn(returns);
        return appResponse;
    }

    /**
     * 第4步：发起人信息录入页点击提交或者保存草稿，来到成功提示页
     */
    @GetMapping("/start-step-4.html")
    public String startstep4(HttpSession session){
        MemberRespsonVo loginUser = (MemberRespsonVo) session.getAttribute("loginUser");
        String projectToken = (String) session.getAttribute("projectToken");

        BaseVo baseVo = new BaseVo();
        baseVo.setAccessToken(loginUser.getAccessToken());
        baseVo.setProjectToken(projectToken);
        AppResponse<String> submit = projectFeignService.submit(baseVo);
        if(submit.getCode() == 0){
            //项目保存成功了
            return "/protected/project/start-step-4";
        }else {
            return "redirect:/start-step-3.html";
        }

    }

}
