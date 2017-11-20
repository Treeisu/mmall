package com.mmall.controller.portal;

import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;



/**
 * 
 * @ClassName: ShippingController
 * @Description: TODO 用户地址管理
 * @author Barry
 * @date 2017年11月20日 下午6:54:58
 *
 */
@Controller
@RequestMapping("/shipping")
public class ShippingController {
	@Autowired
	private IShippingService iShippingService;
	
	/**
	 * 
	 * @Title: add
	 * @Description: TODO 添加地址
	 * @param @param session
	 * @param @param shipping
	 * @param @return    返回该条地址记录的主键id
	 * @return ServerResponse<Map<String,Integer>>    
	 * @throws
	 */
	@RequestMapping(value="/add",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Map<String, Integer>> add(HttpSession session,Shipping shipping){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<Map<String, Integer>> response=iShippingService.add(user.getId(), shipping);						
		return response;		
	}
	/**
	 * 
	 * @Title: del
	 * @Description: TODO 用户删除收货地址
	 * @param @param session
	 * @param @param shippingId
	 * @param @return    
	 * @return ServerResponse<String>    
	 * @throws
	 */
	@RequestMapping(value="/del",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> del(HttpSession session,Integer shippingId){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<String> response=iShippingService.del(user.getId(), shippingId);						
		return response;		
	}
	/**
	 * 
	 * @Title: update
	 * @Description: TODO 更新收货地址
	 * @param @param session
	 * @param @param shipping
	 * @param @return    
	 * @return ServerResponse<Map<String,Integer>>    
	 * @throws
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> update(HttpSession session,Shipping shipping){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<String> response=iShippingService.update(user.getId(), shipping);						
		return response;		
	}
	/**
	 * 
	 * @Title: select
	 * @Description: TODO 查询单条地址记录
	 * @param @param session
	 * @param @param shippingId
	 * @param @return    
	 * @return ServerResponse<String>    
	 * @throws
	 */
	@RequestMapping(value="/select",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Shipping> select(HttpSession session,Integer shippingId){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<Shipping> response=iShippingService.select(user.getId(), shippingId);						
		return response;		
	}
	/**
	 * 
	 * @Title: list
	 * @Description: TODO 查询收货地址的列表 【分页】
	 * @param @param session
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return    
	 * @return ServerResponse<PageInfo<Shipping>>    
	 * @throws
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<PageInfo<Shipping>> list(HttpSession session,
												@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,
												@RequestParam(value="pageSize",defaultValue="5")Integer pageSize){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<PageInfo<Shipping>> response=iShippingService.list(user.getId(), pageNum, pageSize);						
		return response;		
	}
}
