package com.atguigu.scw.ui.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.atguigu.scw.common.bean.ResponseVo;
import com.atguigu.scw.vo.response.ProjectDetailsResponseVo;

@FeignClient(value = "SCW-PROJECT")
public interface ProjectInfoControllerFeign {
	@GetMapping("/project/all/index")
	public ResponseVo getAllIndex();
	@GetMapping("/project/info/detail")
	public ResponseVo<ProjectDetailsResponseVo> getInfoDetail(@RequestParam("proId")Integer proId);
}
