package com.atguigu.front.vo.pay;

import lombok.Data;

@Data
public class PayVo {
    private String out_trade_no;
    private String subject;
    private String total_amount;
    private String body;

}
