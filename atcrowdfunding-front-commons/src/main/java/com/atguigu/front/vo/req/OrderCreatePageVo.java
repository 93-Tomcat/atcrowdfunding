package com.atguigu.front.vo.req;

import lombok.Data;

@Data
public class OrderCreatePageVo {
    private String address;//收货地址;是详细地址，不是id

    private String invoice;//0 - 不开发票， 1 - 开发票

    private String invoictitle;//发票抬头

    private String remark;//备注

}
