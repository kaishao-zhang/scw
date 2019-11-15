package com.atguigu.scw.ui.contorller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.atguigu.scw.common.bean.ResponseVo;
import com.atguigu.scw.common.vo.response.ProjectResposeVo;
import com.atguigu.scw.ui.service.ProjectInfoControllerFeign;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DispatcherController {
	@Autowired
	ProjectInfoControllerFeign projectInfoControllerFeign;
	
	@RequestMapping(value= {"/" , "/index" , "/index.html"})
	public String toIndexPage(Model model) {
		//远程调用project项目  查询项目列表
		ResponseVo<List<ProjectResposeVo>> vo = projectInfoControllerFeign.getAllIndex();
		//将项目列表存到request域中
		log.info("查询到的项目列表：{}", vo);
		/*if("200".equals(vo.getCode())) {
			
		}*/
		model.addAttribute("projects", vo.getData());
		//到index页面获取数据遍历显示
		return "index";
	}
	
	
}
