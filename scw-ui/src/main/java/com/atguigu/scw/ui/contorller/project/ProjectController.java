package com.atguigu.scw.ui.contorller.project;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.atguigu.scw.common.bean.ResponseVo;
import com.atguigu.scw.ui.service.ProjectInfoControllerFeign;
import com.atguigu.scw.vo.response.ProjectDetailsResponseVo;

@Controller
@RequestMapping("/project")
public class ProjectController {
	@Autowired
	ProjectInfoControllerFeign projectInfoControllerFeign;
	@GetMapping("/projectDetails")
	public String projectDetails(Integer id,HttpSession session) {
		ResponseVo<ProjectDetailsResponseVo> proDetailsResponseVo = projectInfoControllerFeign.getInfoDetail(id);
		session.setAttribute("project", proDetailsResponseVo.getData());
		System.out.println("ui得到的项目详情："+proDetailsResponseVo);
		return "project/project";
	}
}
