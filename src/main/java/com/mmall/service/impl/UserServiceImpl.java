package com.mmall.service.impl;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
@Service("iUserService")
public class UserServiceImpl implements IUserService {
	@Autowired
	private  UserMapper userMapper;
	
	/**
	 * 用户登录
	 */
	@Override
	public ServerResponse<User> login(String username, String password) {
		if(userMapper.checkUsername(username)==0){
			return ServerResponse.createByErrorMessage("用户名不存在!");
		}					
		//TODO  使用密码进行登录【需要对密码进行加密才能验证】
		User user=userMapper.selectLogin(username, MD5Util.MD5EncodeUtf8(password));
		if(user==null){
			return ServerResponse.createByErrorMessage("密码输入错误");	
		}				
		//查询到用户，将其密码置空不返回给前端
		user.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccessMessage("登录成功", user);
	}
	/**
	 * 用户注册
	 */
	@Override
	public ServerResponse<String> register(User user) {
		/**
		 * 以下两步校验是为了防止恶意调用注册接口 而进行的校验
		 * 一般在用户注册页面输入框输入完就应该ajax请求后台进行校验
		 */
		//校验 用户名 
		if(this.checkValid(user.getUsername(), Const.USERNAME).isError()){
			return ServerResponse.createByErrorMessage("该用户名已存在");
		}					
		//校验Email
		if(this.checkValid(user.getEmail(), Const.EMAIL).isError()){
			return ServerResponse.createByErrorMessage("该邮箱已注册");
		}					
		//设置用户 身份字段
		user.setRole(Const.Role.ROLE_CUSTOMER);		
		//对用户的密码进行加密
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));		
		//进行保存user
		int reStat=userMapper.insert(user);
		if(reStat==0){//影响行数为0，说明插入失败
			return ServerResponse.createByErrorMessage("系统异常，注册失败");
		}else{
			return ServerResponse.createBySuccessMessage("注册成功");	
		}
				
	}
	/**
	 * 
	 * @Title: checkValid
	 * @Description: TODO 实时校验用户名和email
	 * @param @param str
	 * @param @param type
	 * @param @return    
	 * @return ServerResponse<String> 存在是返回错误信息，不存在返回正确信息  
	 * @throws
	 */
	@Override
	public ServerResponse<String> checkValid(String str,String type) {
		//判断是否为空或者是空格字符串""
		if(StringUtils.isBlank(type)){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "存在异常，参数错误");		
		}else{
			switch (type) {
			case Const.USERNAME:
				//校验 用户名
				if(userMapper.checkUsername(str)>0){//个数大于0说明已经存在该用户
					return ServerResponse.createByErrorMessage("该用户名已存在");
				}
				break;
			case Const.EMAIL:
				//校验Email
				if(userMapper.checkEmail(str)>0){//个数大于0说明已经存在该用户
					return ServerResponse.createByErrorMessage("该邮箱已注册");
				}
				break;			
			default:
				break;
			}
		}			
		return ServerResponse.createBySuccessMessage("校验成功，可注册")	;						
	}
	/**
	 * 查询用户密保问题
	 */
	@Override
	public ServerResponse<String> selectQuestion(String username) {
		// TODO Auto-generated method stub
		//校验用户名
		if(this.checkValid(username, Const.USERNAME).isSuccess()){
			return ServerResponse.createByErrorMessage("用户不存在");
		}else{
			String question=userMapper.selectQuestionByUsername(username);
			if(StringUtils.isNotBlank(question))
				return ServerResponse.createBySuccessMessage("查询成功",question);
			else
				return ServerResponse.createByErrorMessage("该账号的密保问题为空");			
		}
	}
	/**
	 * 忘记密码回答密保问题
	 */
	@Override
	public ServerResponse<String> checkAnswer(String username, String question, String answer) {
		// TODO Auto-generated method stub
		//查询符合条件的记录个数
		int num=userMapper.checkAnswer(username, question, answer);
		if(num<=0){//说明没有查询到记录，回答是错误的
			return ServerResponse.createByErrorMessage("密保问题答案不正确");
		}else{
			//声明一个token，采用uuid生成
			String forgetToken=UUID.randomUUID().toString();
			//将token放进本地缓存
			TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, forgetToken);
			return ServerResponse.createBySuccessMessage("回答正确",forgetToken);
		}		
	}
	/**
	 * 忘记密码重置密码
	 */
	@Override
	public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
		// TODO Auto-generated method stub
		//校验token
		if(StringUtils.isBlank(forgetToken)){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数错误，token为空值");
		}else{
			//校验username
			if(this.checkValid(username, Const.USERNAME).isSuccess()){
				return ServerResponse.createByErrorMessage("重置错误，该用户名不存在");
			}else {
				//根据username从缓存中获得该token
				String token=TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
				if(StringUtils.isBlank(token)){
					return ServerResponse.createByErrorMessage("token数据已失效");
				}else{
					//token验证通过
					if(StringUtils.equals(forgetToken, token)){//后台获得的token和传进来的token对比
						passwordNew=MD5Util.MD5EncodeUtf8(passwordNew);//对密码加密
						//进行update并返回影响行数
						int rowCount=userMapper.updatePasswordByUsername(username, passwordNew);
						if(rowCount>0){
							return ServerResponse.createBySuccessMessage("修改密码成功");
						}else{
							return ServerResponse.createByErrorMessage("密码修改失败，数据库写入异常");
						}
					}else{
						return ServerResponse.createByErrorMessage("token数据验证错误");
					}					
				}				
			}		
		}
	}
	/**
	 * 登录重置密码
	 */
	@Override
	public ServerResponse<String> resetPassword(String passwordOld, String passwordNew,User user) {
		// TODO Auto-generated method stub
		//防止横向越权  校验旧密码是否正确   需要根据用户id和密码查询记录
		int num=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
		if(num==0){//没有查询到记录，说明旧密码错误
			return ServerResponse.createByErrorMessage("原密码输入错误，修改密码失败");
		}else{
			user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
			//进行更新，返回影响行数
			int rowCount=userMapper.updateByPrimaryKeySelective(user);
			if(rowCount>0){
				return ServerResponse.createBySuccessMessage("密码修改成功");
			}else{
				return ServerResponse.createByErrorMessage("数据库写入异常，密码修改失败");
			}
		}
	}
	/**
	 * 更新用户个人信息
	 */
	@Override
	public ServerResponse<User> updateInformation(User user) {
		// TODO Auto-generated method stub		
		//email 校验新的email是否被他人占用
		int num=userMapper.checkEmailUserId(user.getEmail(), user.getId());
		if(num>0){//查询到记录 说明该email被其他人占用了
			return ServerResponse.createByErrorMessage("该邮箱已被其他账号占用");
		}else{
			//将重新new一个user对象 只更新对应的字段，减少数据库压力
			User updateUser=new User();
			updateUser.setId(user.getId());
			updateUser.setEmail(user.getEmail());
			updateUser.setPhone(user.getPhone());
			updateUser.setQuestion(user.getQuestion());
			updateUser.setAnswer(user.getAnswer());
			//返回影响行数
			int rowCount=userMapper.updateByPrimaryKeySelective(updateUser);
			if(rowCount>0){
				user=userMapper.selectByPrimaryKey(user.getId());
				user.setPassword(StringUtils.EMPTY);
				return ServerResponse.createBySuccessMessage("个人信息更新成功",user);
			}else{
				return ServerResponse.createByErrorMessage("数据库写入异常，个人信息更新失败");
			}
		}
	}
	/**
	 * 获得用户当前的个人信息
	 */
	@Override
	public ServerResponse<User> getInformation(int userId) {
		// TODO Auto-generated method stub
		User u=userMapper.selectByPrimaryKey(userId);
		if(u==null){
			return ServerResponse.createByErrorMessage("当前用户信息未查询到，请重新登陆");
		}
		u.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccessMessage("查询到用户个人信息", u);
	}
	@Override
	public ServerResponse<String> checkAdminRole(User user) {
		// TODO Auto-generated method stub
		if(user.getRole().intValue()==Const.Role.ROLE_ADMIN){
			return ServerResponse.createBySuccessMessage("当前用户是管理员！");
		}else{
			return ServerResponse.createByErrorMessage("当前用户非管理员！");
		}
	}		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
