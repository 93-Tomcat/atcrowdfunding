package com.atguigu.front.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class MemberRespsonVo {


    @ApiModelProperty("访问令牌，登录以后所有请求都必须携带访问令牌")
    private String accessToken;//访问令牌

    private String loginacct;

    private String username;

    private String email;

    private String authstatus;

    private String usertype;

    private String realname;

    private String cardnum;

    private String accttype;
}
