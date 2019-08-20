package com.atguigu.atcrowdfunding.app.service.feign;

import com.atguigu.front.bean.TOrder;
import com.atguigu.front.common.AppResponse;
import com.atguigu.front.vo.pay.PayVo;
import com.atguigu.front.vo.req.OrderCreateVo;
import com.atguigu.front.vo.req.OrderListVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/order")
@FeignClient("ATCROWDFUNDING-ORDER")
public interface OrderFeignService {

    @PostMapping("/create")
    public AppResponse<TOrder> create(@RequestBody OrderCreateVo createVo);

    @PostMapping("/pay")
    public String pay(PayVo vo) throws Exception;

    @PostMapping("/list")
    public AppResponse<List<OrderListVo>> orderList(@RequestParam("accessToken") String accessToken);

    @GetMapping("/memberOrder")
    public AppResponse<TOrder> getOrder(@RequestParam("accessToken") String accessToken,@RequestParam("orderNum") String orderNum);
}
