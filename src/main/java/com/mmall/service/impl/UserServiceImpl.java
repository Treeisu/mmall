package com.mmall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;

public class UserServiceImpl implements IUserService {
	@Autowired
	private  UserMapper userMapper;
	@Override
	public ServerResponse<User> login(String name, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
