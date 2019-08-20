package com.atguigu.front.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel
public class AppResponse<T> {

    @ApiModelProperty("请求是否成功的状态码。0：成功   1-其他：失败")
    private Integer code;

    @ApiModelProperty("成功或者失败的提示")
    private String msg;

    @ApiModelProperty("真正的数据")
    private T data;


    //快速返回成功
    public static<T> AppResponse<T> success(T data){
        AppResponse<T> appResponse = new AppResponse<>();
        appResponse.setCode(0);
        appResponse.setMsg("操作成功");
        appResponse.setData(data);
        return appResponse;
    }

    public static<T> AppResponse<T> fail(){
        AppResponse<T> appResponse = new AppResponse<>();
        appResponse.setCode(1);
        appResponse.setMsg("操作失败");
        return appResponse;
    }


    /**
     * 如何设计链式调用。总是返回这个对象，
     * @param msg
     * @return
     */
    public AppResponse msg(String msg){
        this.setMsg(msg);
        return this;
    }

    public AppResponse code(Integer code){
        this.setCode(code);
        return this;
    }

    public AppResponse data(T code){
        this.setData(data);
        return this;
    }


}
