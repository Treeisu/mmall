package com.mmall.controller.backend;

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
 * @ClassName: UserManagerController
 * @Description: TODO 管理员登录系统
 * @author Barry
 * @date 2017年11月7日 下午6:57:09
 *
 */
@Controller
@RequestMapping("/manage/user")
public class UserManagerController {
	@Autowired
	private IUserService iuserService;
	/**
	 * 
	 * @Title: login
	 * @Description: TODO  管理员进行登陆
	 * @param @param username
	 * @param @param password
	 * @param @param session
	 * @param @return    
	 * @return ServerResponse<User>    
	 * @throws
	 */
	@RequestMapping(value="/login",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(String username,String password,HttpSession session){
		ServerResponse<User> response=iuserService.login(username, password);
		if(response.isSuccess()){//登陆成功
			User user=response.getData();
			if(user.getRole()==Const.Role.ROLE_ADMIN){//用户角色是管理员时
				session.setAttribute(Const.CURRENT_USER, user);
				return response;
			}else{
				return ServerResponse.createByErrorMessage("该用户非管理员,无法登陆后台系统");
			}
		}
		return response;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
