package com.atguigu.front.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

@ApiModel
@Data
public class OrderCreateVo {
    private String accessToken;//用户登录以后的访问令牌
    private Integer projectid;//支持的项目的id
    private Integer returnid;//支持的档位的id


    private Integer money;//订单总额；支持金额
    private Integer rtncount;//回报数量


    private String address;//收货地址;是详细地址，不是id

    private String invoice;//0 - 不开发票， 1 - 开发票

    private String invoictitle;//发票抬头

    private String remark;//备注
}
