package com.atguigu.alipaytest.vo;

import lombok.Data;

@Data
public class PayResultVo {
    private String charset;
    private String out_trade_no;//订单号
    private String method;
    private String total_amount;
    private String sign;
    private String trade_no;//支付交易流水
    private String auth_app_id;//
    private String version;
    private String app_id;
    private String sign_type;
    private String seller_id;//商家id
    private String timestamp;
}
