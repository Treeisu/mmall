package com.mmall.controller.portal;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;

/**
 * 
 * @ClassName: CartController
 * @Description: TODO 客户购物车相关请求
 * @author Barry
 * @date 2017年11月15日 上午9:55:21
 *
 */
@Controller
@RequestMapping("/cart")
public class CartController {
	@Autowired
	private ICartService iCartService;
	
	/**
	 * 	
	 * @Title: add
	 * @Description: TODO 添加购物车
	 * @param @param session
	 * @param @param count
	 * @param @param productId
	 * @param @return    
	 * @return ServerResponse<CartVo>    
	 * @throws
	 */
	@RequestMapping(value="/add",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<CartVo> add(HttpSession session,Integer count,Integer productId){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<CartVo> response=iCartService.add(user.getId(), count, productId);		
		return response;		
	}
	
	@RequestMapping(value="/update",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<CartVo> update(HttpSession session,Integer count,Integer productId){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<CartVo> response=iCartService.update(user.getId(), count, productId);		
		return response;		
	}
	
	
	
	
	
}
