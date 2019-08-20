package com.atguigu.front.vo.resp;


import com.atguigu.front.bean.TProject;
import lombok.Data;

import java.util.List;


public class ProjectAllInfoVo extends TProject {
    private String imgurl;

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
