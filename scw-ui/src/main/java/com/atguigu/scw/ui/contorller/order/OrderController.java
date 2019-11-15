package com.atguigu.scw.ui.contorller.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.scw.common.bean.ResponseVo;
import com.atguigu.scw.common.utils.AppDateUtils;
import com.atguigu.scw.common.vo.response.TReturn;
import com.atguigu.scw.common.vo.response.UserResponseVo;
import com.atguigu.scw.ui.bean.TMemberAddress;
import com.atguigu.scw.ui.bean.TOrder;
import com.atguigu.scw.ui.config.AlipayConfig;
import com.atguigu.scw.ui.service.OrderControllerFeign;
import com.atguigu.scw.ui.service.UserRegLogControllerFeign;
import com.atguigu.scw.vo.request.OrderFormInfoSubmitVo;
import com.atguigu.scw.vo.response.ProjectDetailsResponseVo;

@Controller
@RequestMapping("/order")
public class OrderController {
	@Autowired
	UserRegLogControllerFeign userRegLogFeign;
	@Autowired
	OrderControllerFeign orderControllerFeign;

	@GetMapping("/support")
	public String support(Integer rtnid, HttpSession session, Model model) {
		ProjectDetailsResponseVo vo = (ProjectDetailsResponseVo) session.getAttribute("project");
		List<TReturn> returnList = vo.getReturns();
		if (!CollectionUtils.isEmpty(returnList)) {
			for (TReturn tReturn : returnList) {
				boolean flag = ((tReturn.getId() + "").equals(rtnid + ""));
				if (flag) {
					session.setAttribute("return", tReturn);
				}
			}
		}

		return "order/pay-step-1";
	}

	@GetMapping("/pay-step-2")
	public String payStep2(Integer count, HttpSession session, Model model, @RequestHeader("referer") String referer) {
		System.out.println("接收到的数量为：" + count);
		// 判断登录状态，如果未登录先去登录
		UserResponseVo user = (UserResponseVo) session.getAttribute("user");
		if (user == null) {
			model.addAttribute("errorMsg", "结账前请登录");
			session.setAttribute("ref", referer);
			return "user/login";
		} else {
			// 根据access token查找用户的地址信息
			String accessToken = user.getAccesstoken();
			ResponseVo<List<TMemberAddress>> userInfoAddressList = userRegLogFeign.getUserInfoAddress(accessToken);
			List<TMemberAddress> tMemberAddresses = userInfoAddressList.getData();
			System.out.println("从userInfoController中获取的tMemberAddresses为：" + tMemberAddresses);
			model.addAttribute("addresses", tMemberAddresses);
			// 将接受到的count也放入request域中
			model.addAttribute("count", count);
			// 计算总价钱
			TReturn tReturn = (TReturn) session.getAttribute("return");
			// 运费
			Integer freight = tReturn.getFreight();
			// 支持金额
			Integer supportmoney = tReturn.getSupportmoney();
			// 将所有的钱转为bigdecimal类型
			BigDecimal countBd = new BigDecimal("" + count);
			BigDecimal freightBd = new BigDecimal("" + freight);
			BigDecimal supportmoneyBd = new BigDecimal("" + supportmoney);
			BigDecimal totalPriceDb = supportmoneyBd.multiply(countBd).add(freightBd);
			double totalPrice = totalPriceDb.doubleValue();
			model.addAttribute("totalPrice", totalPrice);
			return "order/pay-step-2";
		}
	}

	@ResponseBody
	@PostMapping(value = "/checkout", produces = "text/html") // produces
	// 设置响应报文的格式[response.setContentType("text/html;charset=UTF-8")]
	public String checkout(OrderFormInfoSubmitVo vo, HttpSession session) {
		System.out.println("接收到的OrderFormInfoSubmitVo的信息为：" + vo);
		// 封装订单信息
		TOrder order = new TOrder();
		BeanUtils.copyProperties(vo, order);// private Integer rtncount;
		// 远程调用order项目将订单数据存到数据库中
		// private Integer memberid; 购买订单的用户，当前登录的用户
		UserResponseVo user = (UserResponseVo) session.getAttribute("user");
		Integer memberid = user.getId();
		order.setMemberid(memberid);
		// private Integer projectid; 当前用户购买的项目id
		ProjectDetailsResponseVo project = (ProjectDetailsResponseVo) session.getAttribute("project");
		Integer projectid = project.getId();
		order.setProjectid(projectid);
		// private Integer returnid;
		TReturn rtn = (TReturn) session.getAttribute("return");
		Integer returnid = rtn.getId();
		order.setReturnid(returnid);
		// private String createdate;设置订单创建的时间
		String createdate = AppDateUtils.getFormatTime();
		order.setCreatedate(createdate);
		// private Integer money; 订单总金额
		Integer money = vo.getRtncount() * rtn.getSupportmoney() + rtn.getFreight();
		order.setMoney(money);
		// private String status; 0代表未付款 1代表已付款 2付款失败 3退款中 4 退款成功
		String status = "0";
		order.setStatus(status);
		System.out.println("传入controller中的order：" + order);
		// 调用远程服务 存到数据库中
		ResponseVo<String> responseVo = orderControllerFeign.createOrder(order, memberid);
		if ("200".equals(responseVo.getCode())) {
			// 保存成功后 再跳转到支付页面
			// 获得初始化的AlipayClient
			AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id,
					AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key,
					AlipayConfig.sign_type);

			// 设置请求参数
			AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
			alipayRequest.setReturnUrl(AlipayConfig.return_url);
			alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

			// 商户订单号，商户网站订单系统中唯一订单号，必填
			String out_trade_no = responseVo.getData();// new
														// String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"),"UTF-8");
			// 付款金额，必填
			String total_amount = money + "";// new
												// String(request.getParameter("WIDtotal_amount").getBytes("ISO-8859-1"),"UTF-8");
			// 订单名称，必填
			String subject = project.getName(); // new
												// String(request.getParameter("WIDsubject").getBytes("ISO-8859-1"),"UTF-8");
			// 商品描述，可空
			String body = vo.getRemark();// new String(request.getParameter("WIDbody").getBytes("ISO-8859-1"),"UTF-8");

			alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"," + "\"total_amount\":\""
					+ total_amount + "\"," + "\"subject\":\"" + subject + "\"," + "\"body\":\"" + body + "\","
					+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

			// 若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
			// alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
			// + "\"total_amount\":\""+ total_amount +"\","
			// + "\"subject\":\""+ subject +"\","
			// + "\"body\":\""+ body +"\","
			// + "\"timeout_express\":\"10m\","
			// + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
			// 请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

			// 请求
			String result = "";
			try {
				result = alipayClient.pageExecute(alipayRequest).getBody();
			} catch (AlipayApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("支付宝客户端加密加签后的自动提交的表单内容：" + result);
			// 输出：以响应体的形式写给浏览器 [方式 1：给重定向报文，方式2：如果写的响应体中包含js代码，让浏览器跳转 ] 浏览器会自动跳转到支付宝的支付页面
			// out.println(result);
			return result;// 1、不希望视图解析器根据返回结果查找页面[@ResponseBody] 2、返回的是json格式，不希望返回json 希望返回的是html格式的响应报文
		} else {
			return "xxxx";
		}

	}
	// 用户支付完后 支付宝回调的方法[支付宝返回支付结果时调用我们自己编写的方法 将结果传递给此方法]
	// 异步通知方法:修改订单状态

	@GetMapping("/notify_url")
	public String notifyUrl(HttpServletRequest request) throws Exception {
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}

		boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset,
				AlipayConfig.sign_type); // 调用SDK验证签名

		// ——请在这里编写您的程序（以下代码仅作参考）——

		/*
		 * 实际验证过程建议商户务必添加以下校验： 1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
		 * 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额）， 3、校验通知中的seller_id（或者seller_email)
		 * 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
		 * 4、验证app_id是否为该商户本身。
		 */
		if (signVerified) {// 验证成功
			// 商户订单号
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

			// 支付宝交易号
			String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

			// 交易状态
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
			// 走到此处代表 结账真正成功后了，更新订单的状态 status 0未支付 1已支付
			orderControllerFeign.updateOrderStatus(out_trade_no, "1");

			if (trade_status.equals("TRADE_FINISHED")) {
				// 判断该笔订单是否在商户网站中已经做过处理
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序

				// 注意：
				// 退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
			} else if (trade_status.equals("TRADE_SUCCESS")) {
				// 判断该笔订单是否在商户网站中已经做过处理
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序

				// 注意：
				// 付款完成后，支付宝系统发送该交易状态通知
			}

			// out.println("success");

		} else {// 验证失败
				// out.println("fail");

			// 调试用，写文本函数记录程序运行情况是否正常
			// String sWord = AlipaySignature.getSignCheckContentV1(params);
			// AlipayConfig.logResult(sWord);
		}

		return "success";
	}

	// 同步通知方法
	@GetMapping("/return_url")
	public String returnUrl(HttpServletRequest request, Model model) throws Exception {
		// 解析支付宝响应的结果
		// 解密 验签

		// 如果验证通过解析数据[用户支付的总金额(安全校验)、订单的编号...]
		// 获取支付宝GET过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}

		boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset,
				AlipayConfig.sign_type); // 调用SDK验证签名

		// ——请在这里编写您的程序（以下代码仅作参考）——
		if (signVerified) {
			// 商户订单号
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

			// 支付宝交易号
			String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

			// 付款金额
			String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
			// 跳转到支付成功页面 给用户响应
			model.addAttribute("out_trade_no", out_trade_no);
			model.addAttribute("trade_no", trade_no);
			model.addAttribute("total_amount", total_amount);
			orderControllerFeign.updateOrderStatus(out_trade_no, "1");
			return "order/checkout";
			// out.println("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);
		} else {
			// out.println("验签失败");//私钥公钥有错误 不匹配
			return "order/error";
		}

	}

}
