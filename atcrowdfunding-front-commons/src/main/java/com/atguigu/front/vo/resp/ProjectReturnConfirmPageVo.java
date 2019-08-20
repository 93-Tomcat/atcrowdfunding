package com.atguigu.front.vo.resp;

import com.atguigu.front.bean.TReturn;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


public class ProjectReturnConfirmPageVo {
    @Setter @Getter
    private ProjectAllAllInfoVo project;
    @Setter @Getter
    private TReturn currentReturn;

    @Getter
    @Setter
    private String name;
    @Setter @Getter
    private String memberName;
    @Setter @Getter
    private String content;//回报的内容
    @Setter @Getter
    private Integer num;//购买几个
    @Setter @Getter
    private Integer supportmoney;
    @Setter @Getter
    private Integer freight;

    private Integer totalPrice;

    public Integer getTotalPrice() {
        if(num!=null){
            totalPrice = num*supportmoney+freight;
        }else {
            totalPrice = 0;
        }
        return totalPrice;
    }
}
