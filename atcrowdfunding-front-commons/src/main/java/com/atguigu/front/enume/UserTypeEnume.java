package com.atguigu.front.enume;

/**
 * 用户类型枚举
 */
public enum UserTypeEnume {
    PERSONAL("0","个人"),
    COMPANY("1","企业");

    // 用户类型: 0 - 个人， 1 - 企业
    private String code;
    private String msg;

    private UserTypeEnume(String code,String msg){
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
