package com.mmall.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @ClassName: UserController
 * @Description: TODO
 * @author Barry
 * @date 2017年11月6日 下午3:14:21
 *
 */
@Controller
@RequestMapping("/user/")
public class UserController {
	/**
	 * 
	 * @Title: login 用户登录
	 * @Description: TODO 
	 * @param @param name
	 * @param @param password
	 * @param @param session
	 * @param @return    
	 * @return Object    
	 * @throws
	 */
	@RequestMapping(value="login",method=RequestMethod.POST)
	@ResponseBody
	public Object login(String name,String password,HttpSession session){
		return null;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
