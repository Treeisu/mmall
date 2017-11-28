package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.mmall.controller.portal.OrderController;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {
	@SuppressWarnings("unused")
	private Logger logger=LoggerFactory.getLogger(OrderController.class);
	@Autowired
	private IOrderService iOrderService;
	@Autowired
	private IUserService iUserService;
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/list",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<PageInfo> list(HttpSession session,@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,@RequestParam(value="pageSize",defaultValue="10")Integer pageSize){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){//该用户是管理员，查询所有的订单
			ServerResponse response=iOrderService.manageGetOrderList(pageNum, pageSize);
			return response;
		}
		return ServerResponse.createByErrorMessage("您非管理员，无法查看顾客所有订单！");	
	}
	@RequestMapping(value="/detail",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<OrderVo> detail(HttpSession session,Long orderNo){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			ServerResponse<OrderVo> response=iOrderService.manageGetOrderDetail(orderNo);			
			return response;		
		}else{
			return ServerResponse.createByErrorMessage("您非管理员，无法查看订单详情！");	
		}
		
	}
	/**
	 * 
	 * @Title: searchOrder
	 * @Description: TODO 管理员搜索订单
	 * @param @param session
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @param orderNo
	 * @param @return    
	 * @return ServerResponse<PageInfo>    
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/search",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<PageInfo> searchOrder(HttpSession session,@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,@RequestParam(value="pageSize",defaultValue="10")Integer pageSize,Long orderNo){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			ServerResponse<PageInfo> response=iOrderService.manageSearch(pageNum, pageSize, orderNo);			
			return response;		
		}else{
			return ServerResponse.createByErrorMessage("您非管理员，无法查看订单详情！");	
		}
		
	}
	/**
	 * 
	 * @Title: sendGoods
	 * @Description: TODO 发货
	 * @param @param session
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @param orderNo
	 * @param @return    
	 * @return ServerResponse<String>    
	 * @throws
	 */
	@RequestMapping(value="/send_goods",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> sendGoods(HttpSession session,Long orderNo){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			ServerResponse<String> response=iOrderService.sendGoods(orderNo);			
			return response;		
		}else{
			return ServerResponse.createByErrorMessage("您非管理员，无法操作订单！");	
		}
		
	}
}
