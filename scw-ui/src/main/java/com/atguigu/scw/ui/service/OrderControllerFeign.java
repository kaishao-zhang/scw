package com.atguigu.scw.ui.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.atguigu.scw.common.bean.ResponseVo;
import com.atguigu.scw.ui.bean.TOrder;

@FeignClient(value = "SCW-ORDER")
public interface OrderControllerFeign {
	@PostMapping("/order/create")
	public ResponseVo<String> createOrder(@RequestBody TOrder order, @RequestParam("memberid") Integer memberid);

	@PostMapping("/order/pay")
	public ResponseVo<String> updateOrderStatus(@RequestParam("out_trade_no")String out_trade_no,@RequestParam("status") String status);
}
