package com.atguigu.atcrowfunding.order.controller;

import com.atguigu.atcrowfunding.order.service.OrderService;
import com.atguigu.front.common.AppResponse;
import com.atguigu.front.enume.OrderStatusEnumes;
import com.atguigu.front.vo.pay.PayAsyncVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "支付模块")
@RequestMapping("/order/pay")
@RestController
public class PayController {

    @Autowired
    OrderService orderService;

    @ApiOperation("支付宝支付")
    @PostMapping("/alipay/success")
    public String alipay(PayAsyncVo vo){
        //
        String out_trade_no = vo.getOut_trade_no();
        orderService.updateOrder(out_trade_no,OrderStatusEnumes.PAYED);
        return "success";
    }

    @ApiOperation("微信支付")
    @PostMapping("/weixin")
    public AppResponse<String> weixin(){
        return null;
    }

}
