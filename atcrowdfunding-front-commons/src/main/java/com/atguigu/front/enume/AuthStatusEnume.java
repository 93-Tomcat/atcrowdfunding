package com.atguigu.front.enume;

import lombok.Data;

/**
 * 实名认证状态
 */

public enum  AuthStatusEnume {
    UNAUTH("0","未实名认证"),
    AUTHING("1","实名认证中"),
    AUTHED("2","已实名认证");


    //实名认证状态 0 - 未实名认证， 1 - 实名认证申请中， 2 - 已实名认证
    private String code;//给数据库保存
    private String msg;//给人看的

    private AuthStatusEnume(String code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
