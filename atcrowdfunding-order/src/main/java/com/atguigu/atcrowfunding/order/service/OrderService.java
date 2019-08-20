package com.atguigu.atcrowfunding.order.service;

import com.atguigu.front.bean.TOrder;
import com.atguigu.front.enume.OrderStatusEnumes;
import com.atguigu.front.vo.req.OrderCreateVo;
import com.atguigu.front.vo.req.OrderListVo;

import java.util.List;

public interface OrderService {

    TOrder createOrder(OrderCreateVo createVo);

    void updateOrder(String out_trade_no, OrderStatusEnumes payed);


    List<OrderListVo> orderList(String accessToken);

    TOrder getOrder(Integer id, String orderNum);

}
