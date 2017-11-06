package com.mmall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
@Service("iUserService")
public class UserServiceImpl implements IUserService {
	@Autowired
	private  UserMapper userMapper;
	@Override
	public ServerResponse<User> login(String username, String password) {
		if(userMapper.checkUsername(username)==0){
			return ServerResponse.createByErrorMessage("用户名不存在!");
		}
		//TODO  使用密码进行登录
		User user=userMapper.selectLogin(username, password);
		if(user==null){
			return ServerResponse.createByErrorMessage("密码输入错误");
		}
		//查询到用户，将其密码置空不返回给前端
		user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
		return ServerResponse.createBySuccessMessage("登录成功", user);
	}
	
	

}
