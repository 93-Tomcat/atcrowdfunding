package com.atguigu.front.exception;

public class UserEmailException extends RuntimeException {

    public UserEmailException(){
        super("用户邮箱已经存在");
    }
}
