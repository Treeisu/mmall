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
		if(response.isSuccess()){//登录成功，存进session
			session.setAttribute(Const.CURRENT_USER,response.getDate());
		}
		return response;		
	}
	/**
	 * 
	 * @Title: logout  用户退出系统
	 * @Description: TODO
	 * @param @param session
	 * @param @return    
	 * @return ServerResponse<String>    
	 * @throws
	 */
	@RequestMapping(value="logout",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> logout(HttpSession session){
		session.removeAttribute(Const.CURRENT_USER);
		return ServerResponse.createBySuccess();		
	}
	/**
	 * 
	 * @Title: register
	 * @Description: TODO  用户注册
	 * @param @param user
	 * @param @return    
	 * @return ServerResponse<String>    
	 * @throws
	 */
	@RequestMapping(value="register",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> register(User user){
		ServerResponse<String> response=iuserService.register(user);		
		return response;		
	}
	/**
	 * 
	 * @Title: checkValid
	 * @Description: TODO  注册时：实时校验用户的用户名和Email
	 * @param @param str
	 * @param @param type
	 * @param @return    
	 * @return ServerResponse<String>    
	 * @throws
	 */
	@RequestMapping(value="check_valid",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> checkValid(String str,String type){		
		ServerResponse<String> response=iuserService.checkValid(str, type);
		return response;		
	}
	/**
	 * 
	 * @Title: getUserInfo
	 * @Description: TODO 获得当前登录的用户的信息
	 * @param @param session
	 * @param @return    
	 * @return ServerResponse<User>    
	 * @throws
	 */
	@RequestMapping(value="get_user_info",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session){		
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user !=null)
			return ServerResponse.createBySuccessMessage(user);
		else
			return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息！");
	}
	/**
	 * 
	 * @Title: forgetGetQuestion
	 * @Description: TODO  获取用户的密保问题
	 * @param @param username
	 * @param @return    
	 * @return ServerResponse<String>    
	 * @throws
	 */
	@RequestMapping(value="forget_get_question",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> forgetGetQuestion(String username){		
		ServerResponse<String> response=iuserService.selectQuestion(username);
		return response;
	}
	/**
	 * 
	 * @Title: forgetCheckAnswer
	 * @Description: TODO 回答密保问题，并进行本地缓存
	 * @param @param username
	 * @param @param question
	 * @param @param answer
	 * @param @return    
	 * @return ServerResponse<String>    
	 * @throws
	 */
	@RequestMapping(value="forget_check_answer",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){		
		ServerResponse<String> response=iuserService.checkAnswer(username, question, answer);
		return response;
	}
	/**
	 * 
	 * @Title: forgetResetPassword
	 * @Description: TODO
	 * @param @param username
	 * @param @param passwordNew
	 * @param @param forgetToken 前端需要返回一个token进行验证
	 * @param @return    
	 * @return ServerResponse<String>    
	 * @throws
	 */
	@RequestMapping(value="forget_reset_password",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){		
		ServerResponse<String> response=iuserService.forgetResetPassword(username, passwordNew, forgetToken);
		return response;
	}
	
	
	
	
	
	
	
	
	
	
}
