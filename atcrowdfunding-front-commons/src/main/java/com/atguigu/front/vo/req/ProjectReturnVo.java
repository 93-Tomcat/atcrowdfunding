package com.atguigu.front.vo.req;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ProjectReturnVo extends BaseVo {



    private String type;

    private Integer supportmoney;

    private String content;

    private Integer count;

    private Integer signalpurchase;

    private Integer purchase;

    private Integer freight;

    private String invoice;

    private Integer rtndate;
}
