package com.atguigu.front.vo;

import com.atguigu.front.bean.TReturn;
import lombok.Data;

import java.util.List;

/**
 * 项目全量信息在redis中的临时存储。收集页面数据
 */
@Data
public class ProjectRedisStorageVo {
    private String accessToken;//访问令牌
    private String projectToken;//项目的临时token；项目的唯一标识


    private Integer memberid;//会员id


    private List<Integer> typeids; //项目的分类id
    private List<Integer> tagids; //项目的标签id


    private String name;//项目名称
    private String remark;//项目简介
    private Long money;//筹资金额
    private Integer day;//筹资天数
    private String headerImage;//项目头部图片
    private List<String> detailsImage;//项目详情图片
    //以上的第一屏的信息


    private List<TReturn> projectReturns;//项目回报

    //发起人信息：自我介绍，详细自我介绍，联系电话，客服电话






}
