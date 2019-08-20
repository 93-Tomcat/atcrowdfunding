package com.atguigu.atcrowfunding.order.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.atguigu.atcrowfunding.order.config.AlipayTemplate;
import com.atguigu.atcrowfunding.order.service.OrderService;
import com.atguigu.front.bean.TMember;
import com.atguigu.front.bean.TOrder;
import com.atguigu.front.common.AppResponse;
import com.atguigu.front.constant.AppConstant;
import com.atguigu.front.vo.pay.PayVo;
import com.atguigu.front.vo.req.OrderCreateVo;
import com.atguigu.front.vo.req.OrderListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;

@Api(tags = "订单模块")
@RequestMapping("/order")
@RestController
public class OrderController {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    OrderService orderService;

    @Autowired
    AlipayTemplate alipayTemplate;

    /**
     * 按照订单号，查出当前用户的订单
     * @param accessToken
     * @param orderNum
     * @return
     */
    @ApiOperation("按照订单号查询订单详情")
    @GetMapping("/memberOrder")
    public AppResponse<TOrder> getOrder(@RequestParam("accessToken") String accessToken,@RequestParam("orderNum") String orderNum){
        String s = redisTemplate.opsForValue().get(AppConstant.MEMBER_LOGIN_CACHE_PREFIX + accessToken);
        TMember member = JSON.parseObject(s, TMember.class);
        TOrder order = orderService.getOrder(member.getId(),orderNum);
        return AppResponse.success(order);
    }


    @ApiOperation("查询用户的最新5个订单")
    @PostMapping("/list")
    public AppResponse<List<OrderListVo>> orderList(@RequestParam("accessToken") String accessToken){
        List<OrderListVo> orderListVos = orderService.orderList(accessToken);
        return AppResponse.success(orderListVos);
    }


    @ApiOperation("取消订单")
    @PostMapping("/cancle")
    public AppResponse<String> cancle(){
        return null;
    }

    @ApiOperation("创建订单")
    @PostMapping("/create")
    public AppResponse<TOrder> create(@RequestBody OrderCreateVo createVo){
        //

        String accessToken = createVo.getAccessToken();
        String s = redisTemplate.opsForValue().get(AppConstant.MEMBER_LOGIN_CACHE_PREFIX + accessToken);
        if(StringUtils.isEmpty(s)){
            return AppResponse.fail().msg("请先登录");
        }


        //创建出的真正订单
        TOrder order = orderService.createOrder(createVo);


        return AppResponse.success(order);
    }

    @ApiOperation("立即付款")
    @PostMapping(value = "/pay",produces = "text/html;charset=utf-8")
    public String pay(@RequestBody PayVo vo) throws AlipayApiException {

        //调用支付生成支付页的内容
        String pay = alipayTemplate.pay(vo);
        System.out.println("阿里支付返回的页面："+pay);

        //其他只要把这个页面展示在浏览器上自动跳转到支付页
        return pay;
    }
}
