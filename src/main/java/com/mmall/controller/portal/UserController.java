package com.mmall.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;

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
	@Autowired
	private IUserService iuserService;
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
	public ServerResponse<User> login(String username,String password,HttpSession session){
		ServerResponse<User> response=iuserService.login(username, password);
		if(response.isSuccess()){
			session.setAttribute("", "");
		}
		return null;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
