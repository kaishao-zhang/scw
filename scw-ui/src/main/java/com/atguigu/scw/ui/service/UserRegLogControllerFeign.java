package com.atguigu.scw.ui.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.atguigu.scw.common.bean.ResponseVo;
import com.atguigu.scw.common.vo.response.UserResponseVo;
import com.atguigu.scw.ui.bean.TMemberAddress;

@FeignClient(value="SCW-USER")
public interface UserRegLogControllerFeign {
	@PostMapping("/user/login")
	public ResponseVo<UserResponseVo> login(@RequestParam("loginacct")String loginacct,@RequestParam("userpswd") String userpswd);
	@GetMapping("/user/info/address")
	public ResponseVo<List<TMemberAddress>> getUserInfoAddress(@RequestParam("accessToken")String accessToken);
}
