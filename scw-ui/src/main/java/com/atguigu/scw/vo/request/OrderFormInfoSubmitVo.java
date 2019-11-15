package com.atguigu.scw.vo.request;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderFormInfoSubmitVo {
	private String address;//收货地址id
	private String invoice;//0代表不要  1-代表要
	private String invoictitle;//发票抬头
	private String remark;//订单的备注
	private Integer rtncount;//回报数量
}
