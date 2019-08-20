package com.atguigu.front.enume;

public enum AcctTypeEnume {

    ////账户类型: 0 - 企业， 1 - 个体， 2 - 个人， 3 - 政府
    COMPANTY("0","企业"),
    PERSON_COMPANY("1","个体"),
    PERSON("2","个人"),
    GOV("3","政府");

    private String code;//给数据库保存
    private String msg;//给人看的

    private AcctTypeEnume(String code,String msg){
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
