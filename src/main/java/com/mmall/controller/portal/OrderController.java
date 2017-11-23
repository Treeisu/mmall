package com.mmall.controller.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;


/**
 * 
 * @ClassName: OrderController
 * @Description: TODO 订单处理类【包括支付处理】
 * @author Barry
 * @date 2017年11月23日 下午5:13:22
 *
 */
@Controller
@RequestMapping("/order")
public class OrderController {
	
	
	
	public ServerResponse<Object> pay(HttpSession session,Long orderNo,HttpServletRequest request){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}
		//获得项目路径下的upload文件夹【用于暂存二维码】
		String path=request.getSession().getServletContext().getRealPath("upload");
		
		return null;		
	}
	
	
}
