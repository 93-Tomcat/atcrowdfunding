package com.atguigu.atcrowfunding.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.atcrowfunding.order.dao.TOrderMapper;
import com.atguigu.atcrowfunding.order.service.OrderService;
import com.atguigu.front.bean.TMember;
import com.atguigu.front.bean.TOrder;
import com.atguigu.front.bean.TOrderExample;
import com.atguigu.front.constant.AppConstant;
import com.atguigu.front.enume.OrderStatusEnumes;
import com.atguigu.front.vo.req.OrderCreateVo;
import com.atguigu.front.vo.req.OrderListVo;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    TOrderMapper orderMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Override
    public TOrder createOrder(OrderCreateVo createVo) {
        String accessToken = createVo.getAccessToken();

        String s = redisTemplate.opsForValue().get(AppConstant.MEMBER_LOGIN_CACHE_PREFIX + accessToken);
        TMember member = JSON.parseObject(s, TMember.class);
        TOrder order = new TOrder();
        BeanUtils.copyProperties(createVo,order);
        //private String ordernum;//订单号
        //private String createdate;//创建时间
        //private String status;//0 - 待付款， 1 - 交易完成， 9 - 交易关闭
        order.setMemberid(member.getId());
        order.setOrdernum(generateOrderNum(member.getId()));
        order.setCreatedate(formatDate());
        order.setStatus(String.valueOf(OrderStatusEnumes.UNPAY.getCode()));


        orderMapper.insertSelective(order);

        return order;
    }

    @Override
    public void updateOrder(String out_trade_no, OrderStatusEnumes payed) {
        TOrder order = new TOrder();
        order.setStatus(String.valueOf(payed.getCode()));
        TOrderExample example = new TOrderExample();
        example.createCriteria().andOrdernumEqualTo(out_trade_no);
        orderMapper.updateByExampleSelective(order,example);

    }

    @Override
    public List<OrderListVo> orderList(String accessToken) {
        String s = redisTemplate.opsForValue().get(AppConstant.MEMBER_LOGIN_CACHE_PREFIX + accessToken);
        TMember member = JSON.parseObject(s, TMember.class);

        //1、查出最新的五个订单信息；
        List<TOrder> orders = orderMapper.selectLimit5Order(member.getId());
        //2、查出每个订单的项目信息
        List<OrderListVo> orderListVos = new ArrayList<>();


        for (TOrder order : orders) {
            OrderListVo vo = new OrderListVo();
            //vo封装订单和项目对象
            vo.setOrder(order);
            //在查出每个订单对应的项目
            orderListVos.add(vo);
        }

        return orderListVos;
    }

    @Override
    public TOrder getOrder(Integer id, String orderNum) {
        TOrderExample example = new TOrderExample();
        example.createCriteria().andOrdernumEqualTo(orderNum).andMemberidEqualTo(id);
        List<TOrder> orders = orderMapper.selectByExample(example);
        return orders!=null&orders.size()==1?orders.get(0):null;
    }

    private String generateOrderNum(Integer id){
        //时间+用户id+随机数
        long l = System.currentTimeMillis();
        return l+""+id;
    }
    private String formatDate(){
        //时间+用户id+随机数
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }
}
