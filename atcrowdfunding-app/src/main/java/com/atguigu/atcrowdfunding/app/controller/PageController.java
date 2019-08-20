package com.atguigu.atcrowdfunding.app.controller;

import com.atguigu.atcrowdfunding.app.service.feign.OrderFeignService;
import com.atguigu.atcrowdfunding.app.service.feign.ProjectFeignService;
import com.atguigu.front.bean.TProject;
import com.atguigu.front.bean.TReturn;
import com.atguigu.front.common.AppResponse;
import com.atguigu.front.vo.req.OrderListVo;
import com.atguigu.front.vo.resp.MemberRespsonVo;
import com.atguigu.front.vo.resp.ProjectAllAllInfoVo;
import com.atguigu.front.vo.resp.ProjectAllInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class PageController {

    @Autowired
    ProjectFeignService projectFeignService;

    @Autowired
    OrderFeignService orderFeignService;

    /**
     * 1）、只要以.html结尾的都是直接去页面
     * 2）、只要动态请求都不以html结尾
     * @return
     */
    @GetMapping("/member.html")
    public String memberPage(HttpSession session, RedirectAttributes redirectAttributes){
        MemberRespsonVo loginUser = (MemberRespsonVo) session.getAttribute("loginUser");
        if(loginUser == null){
            //没登录
            redirectAttributes.addFlashAttribute("msg","请先登录");
            return "redirect:/login.html";
        }

        return "protected/member";
    }

    @GetMapping("/minecrowdfunding.html")
    public String minecrowdfunding(HttpSession session, RedirectAttributes redirectAttributes,Model model){
        MemberRespsonVo loginUser = (MemberRespsonVo) session.getAttribute("loginUser");
        if(loginUser == null){
            //没登录
            redirectAttributes.addFlashAttribute("msg","请先登录");
            return "redirect:/login.html";
        }


        //1、远程查询当前用户的最新的五个订单信息；
        AppResponse<List<OrderListVo>> orderList = orderFeignService.orderList(loginUser.getAccessToken());

        List<OrderListVo> data = orderList.getData();
        for (OrderListVo orderListVo : data) {
            //2、每一个订单查询自己的项目
            AppResponse<ProjectAllAllInfoVo> detail = projectFeignService.getDetail(orderListVo.getOrder().getProjectid());
            ProjectAllAllInfoVo data1 = detail.getData();
            orderListVo.setProject(data1);
            //3、查询我们这个订单的档位
            List<TReturn> returns = data1.getReturns();
            for (TReturn aReturn : returns) {
                if(aReturn.getId() == orderListVo.getOrder().getReturnid()){
                    orderListVo.setTReturn(aReturn);
                }
            }

        }
        model.addAttribute("ordersList",data);

        return "protected/member/minecrowdfunding";
    }

    @GetMapping(value = {"/","/index.html"})
    public String indexPage(Model model){

        //调用远程服务查出所有项目
       //AppResponse<List<TProject>> index = projectFeignService.getAllIndex();
       // projectFeignService
        AppResponse<List<ProjectAllInfoVo>> allIndex = projectFeignService.getAllIndex();

        model.addAttribute("projects",allIndex.getData());
        return "protected/index";
    }
}
