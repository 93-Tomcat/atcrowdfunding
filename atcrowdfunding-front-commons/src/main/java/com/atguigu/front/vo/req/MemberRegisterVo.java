package com.atguigu.front.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 封装注册提交来的vo数据
 *
 * 如果我们传递的数据在5个以上就抽取vo；
 * 少量数据直接写
 *
 */
@ApiModel
@ToString
@Data
public class MemberRegisterVo {

    @ApiModelProperty(value = "手机号",required = true)
    private String mobile;

    @ApiModelProperty(value = "密码",required = true)
    private String password;

    @ApiModelProperty(value = "邮箱",required = true)
    private String email;

    @ApiModelProperty(value = "验证码",required = true)
    private String code;

}
