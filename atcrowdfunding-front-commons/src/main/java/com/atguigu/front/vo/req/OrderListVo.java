package com.atguigu.front.vo.req;

import com.atguigu.front.bean.TOrder;
import com.atguigu.front.bean.TProject;
import com.atguigu.front.bean.TReturn;
import com.atguigu.front.vo.resp.ProjectAllAllInfoVo;
import com.atguigu.front.vo.resp.ProjectAllInfoVo;
import lombok.Data;

@Data
public class OrderListVo {

    private TOrder order;
    private ProjectAllAllInfoVo project;
    private TReturn tReturn;


}
