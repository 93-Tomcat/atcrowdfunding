package com.atguigu.atcrowdfunding.app.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.atcrowdfunding.app.config.AlipayTemplate;
import com.atguigu.atcrowdfunding.app.service.feign.OrderFeignService;
import com.atguigu.atcrowdfunding.app.service.feign.ProjectFeignService;
import com.atguigu.atcrowdfunding.app.service.feign.UserServiceFeign;
import com.atguigu.front.bean.TMemberAddress;
import com.atguigu.front.bean.TOrder;
import com.atguigu.front.bean.TReturn;
import com.atguigu.front.common.AppResponse;
import com.atguigu.front.constant.AppConstant;
import com.atguigu.front.vo.pay.PayVo;
import com.atguigu.front.vo.req.OrderCreatePageVo;
import com.atguigu.front.vo.req.OrderCreateVo;
import com.atguigu.front.vo.resp.MemberRespsonVo;
import com.atguigu.front.vo.resp.ProjectAllAllInfoVo;
import com.atguigu.front.vo.resp.ProjectReturnConfirmPageVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RequestMapping("/pay")
@Controller
public class PayController {

    @Autowired
    ProjectFeignService projectFeignService;

    @Autowired
    UserServiceFeign userServiceFeign;

    @Autowired
    OrderFeignService orderFeignService;

    @Autowired
    AlipayTemplate alipayTemplate;





    /**
     * 支付第一步，点击支持，跳到回报确认页
     *
     * @Param retId 档位id
     * @Param projectId 项目id
     */
    @GetMapping("/step-1.html")
    public String payStep1ToReturnConfirmPage(@RequestParam("retId") Integer id,
                                              @RequestParam("projectId") Integer projectId,
                                              Model model,
                                              HttpSession session) {
        ProjectReturnConfirmPageVo pageVo = new ProjectReturnConfirmPageVo();
        //1、查出这档位/项目的详细信息，封装成vo给页面
        AppResponse<ProjectAllAllInfoVo> detail = projectFeignService.getDetail(projectId);
        //提取远程查到的项目详情
        ProjectAllAllInfoVo data = detail.getData();
        TReturn currentReturn = null;
        List<TReturn> returns = data.getReturns(); //提取回报是哪个
        for (TReturn aReturn : returns) {
            if (aReturn.getId() == id) {
                currentReturn = aReturn;
            }
        }


        //封装值
        pageVo.setName(data.getName());
        pageVo.setContent(currentReturn.getContent());
        pageVo.setFreight(currentReturn.getFreight());
        pageVo.setMemberName(data.getMemberName() + "宇宙公司");
        pageVo.setNum(1);
        pageVo.setSupportmoney(currentReturn.getSupportmoney());
        pageVo.setProject(data);
        pageVo.setCurrentReturn(currentReturn);


        model.addAttribute("projectVo", pageVo);
        session.setAttribute("project", pageVo);
        //显示支持的是哪个档位的信息
        return "protected/pay/pay-step-1";
    }

    /**
     *
     * @param num
     * @param session
     * @param redirectAttributes  重定向保存数据
     * @param model   转发
     * @return
     */
    @GetMapping("/confirm-order.html")
    public String payStep2ToOrderConfirmPage(
            @RequestParam("num") Integer num,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {
        //获取当前登录的用户
        MemberRespsonVo loginUser = (MemberRespsonVo) session.getAttribute("loginUser");
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("msg", "当前操作需要登录，请先登录");
            return "redirect:/login.html";
        }

        //1、取出session刚才保存的项目信息
        ProjectReturnConfirmPageVo project = (ProjectReturnConfirmPageVo) session.getAttribute("project");
        //更新session之前保存的数量信息
        project.setNum(num);

        //2、调用用户服务查询出用户的收货地址
        AppResponse<List<TMemberAddress>> userAddress = userServiceFeign.getUserAddress(loginUser.getAccessToken());

        model.addAttribute("userAddress", userAddress.getData());

        return "protected/pay/pay-step-2";
    }

    /**
     * 点击立即付款
     * @param vo
     * @param session
     * @return
     *
     * pay.html；响应编码为html
     * pay.json；编码为json；415；
     */
    @ResponseBody
    @PostMapping(value = "/pay")
    public String pay(OrderCreatePageVo vo, HttpSession session,RedirectAttributes attributes) throws Exception {
        //获取当前登录的用户
        MemberRespsonVo loginUser = (MemberRespsonVo) session.getAttribute("loginUser");
        if (loginUser == null) {
            attributes.addFlashAttribute("msg", "当前操作需要登录，请先登录");
            return "redirect:/login.html";
        }
        ProjectReturnConfirmPageVo project = (ProjectReturnConfirmPageVo) session.getAttribute("project");

        //1、调用远程创建订单接口
        OrderCreateVo orderCreateVo = new OrderCreateVo();
        BeanUtils.copyProperties(vo,orderCreateVo);
        /**
         *     private String accessToken;//用户登录以后的访问令牌
         *     private Integer projectid;//支持的项目的id
         *     private Integer returnid;//支持的档位的id
         *     private Integer money;//订单总额；支持金额
         *     private Integer rtncount;//回报数量
         */
        orderCreateVo.setAccessToken(loginUser.getAccessToken());
        orderCreateVo.setProjectid(project.getProject().getId());
        orderCreateVo.setReturnid(project.getCurrentReturn().getId());
        orderCreateVo.setMoney(project.getTotalPrice());
        orderCreateVo.setRtncount(project.getNum());

        //创建完订单
        AppResponse<TOrder> response = orderFeignService.create(orderCreateVo);
        TOrder order = response.getData();
        //2、调用远程的支付服务，准备订单支付用的数据
        PayVo payVo = new PayVo();
        payVo.setBody(new String(project.getProject().getName().getBytes("UTF-8"),"UTF-8"));
        payVo.setOut_trade_no(new String(order.getOrdernum().getBytes("UTF-8"),"UTF-8"));
        payVo.setSubject(new String(project.getProject().getName().getBytes("UTF-8"),"UTF-8"));
        payVo.setTotal_amount(new String(order.getMoney().toString().getBytes("UTF-8"),"UTF-8"));

        //2、调用远程的支付服务，返回支付的页面
        String pay = alipayTemplate.pay(payVo);
        System.out.println("阿里的页面："+pay);

        return pay;
    }


    @GetMapping("/success.html")
    public String paySucessPage(){

        return "redirect:/minecrowdfunding.html";
    }


    /**
     * 将未支付的订单，再次跳到支付页面进行支付
     */
    @ResponseBody
    @GetMapping("/topay")
    public String toPay(@RequestParam("orderNum") String orderNum,HttpSession session) throws UnsupportedEncodingException, AlipayApiException {

        MemberRespsonVo loginUser = (MemberRespsonVo) session.getAttribute("loginUser");
        //1、远程按照订单号，查出订单的信息，继续支付
        AppResponse<TOrder> order = orderFeignService.
                getOrder(loginUser.getAccessToken(), orderNum);

        //2、调用支付方法
        PayVo payVo = new PayVo();
        payVo.setBody(new String("未支付的订单".getBytes("UTF-8"),"UTF-8"));
        payVo.setOut_trade_no(new String(order.getData().getOrdernum().getBytes("UTF-8"),"UTF-8"));

        payVo.setSubject(new String(order.getData().getOrdernum().getBytes("UTF-8"),"UTF-8"));
        payVo.setTotal_amount(new String(order.getData().getMoney().toString().getBytes("UTF-8"),"UTF-8"));

        //2、调用远程的支付服务，返回支付的页面
        String pay = alipayTemplate.pay(payVo);
        return pay;
    }

}
