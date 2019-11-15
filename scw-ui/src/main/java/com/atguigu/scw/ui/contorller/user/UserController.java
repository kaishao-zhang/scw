package com.atguigu.scw.ui.contorller.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.atguigu.scw.common.bean.ResponseVo;
import com.atguigu.scw.common.vo.response.UserResponseVo;
import com.atguigu.scw.ui.service.UserRegLogControllerFeign;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserRegLogControllerFeign userRegLogFeign;
	//用户登录
	@PostMapping("/doLogin")
	public String doLogin(String loginacct,String userpswd,Model model,HttpSession session) {
		ResponseVo<UserResponseVo> responseVo = userRegLogFeign.login(loginacct, userpswd);
		String code = responseVo.getCode();
		if("200".equals(code)) {
			//登录成功，重定向到主页面将登录状态保存到session中
			//获取数据
			UserResponseVo vo = responseVo.getData();
			//使用session时 需要使用springsession解决分布式session数据共享问题
			session.setAttribute("user",vo);
			/*UserResponseVo object = (UserResponseVo) session.getAttribute("user");
			System.out.println("session域中的值1："+object+"session域中的值2："+object2);*/
			String referer = (String) session.getAttribute("ref");
			if(StringUtils.isEmpty(referer)) {
				return "redirect:/index.html";
			}
			session.removeAttribute("ref");
			return "redirect:"+referer;
		}else {
			//登录失败，将错误消息回显，并转发
			model.addAttribute("errorMsg",responseVo.getMessage());
			return "user/login";
		}
	}
}
