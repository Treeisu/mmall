package com.mmall.controller.portal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;


/**
 * 
 * @ClassName: OrderController
 * @Description: TODO 订单处理类【包括支付处理】
 * @author Barry
 * @date 2017年11月23日 下午5:13:22
 *
 */
@Controller
@RequestMapping("/order")
public class OrderController {
	private Logger logger=LoggerFactory.getLogger(OrderController.class);
	@Autowired
	private IOrderService iOrderService;
	
	/**
	 * 
	 * @Title: pay
	 * @Description: TODO 支付接口【支付宝返回一个二维码】
	 * @param @param session
	 * @param @param orderNo
	 * @param @param request
	 * @param @return    
	 * @return ServerResponse<Map<String,String>>    
	 * @throws
	 */
	@RequestMapping(value="/pay",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Map<String, String>> pay(HttpSession session,Long orderNo,HttpServletRequest request){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}
		//获得项目路径下的upload文件夹【用于暂存二维码】
		String path=request.getSession().getServletContext().getRealPath("upload");
		//调用支付宝【当面付】接口，得到结果，如果成功则返回二维码的存放路径
		ServerResponse<Map<String, String>> response=iOrderService.pay(orderNo, user.getId(), path);
		return response;		
	}
	/**
	 * 
	 * @Title: AliPay_CallBack
	 * @Description: TODO 用户扫描二维码，点击支付后，支付宝回调此接口，我们需要返回核对结果【失败或者成功】
	 * @param @param request
	 * @param @return    
	 * @return Object    
	 * @throws
	 */
	@RequestMapping(value="/alipay_callback",method=RequestMethod.GET)
	@ResponseBody
	public String AliPay_CallBack(HttpServletRequest request){
		//获得所有参数
		Map<String,String[]> requestParams=request.getParameterMap();
		requestParams.remove("sign_type");//下面验证时需要删除此参数
		//将参数对对应的值  数组类型转换成字符串【用逗号拼接】
		Map<String,String> params=paramInit(requestParams);
		logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
		ServerResponse<String> response=iOrderService.aliCallBackCheck(params);		
		if(response.isSuccess()){
			return Const.AlipayCallback.RESPONSE_SUCCESS;
		}else{
			return Const.AlipayCallback.RESPONSE_FAILED;	
		}			
	}
	/**
	 * 
	 * @Title: queryOrderPayStatus
	 * @Description: TODO 查询某个订单的支付状态是否是支付成功的
	 * @param @param session
	 * @param @param orderNo
	 * @param @return    
	 * @return ServerResponse<Boolean>    
	 * @throws
	 */
	@RequestMapping(value="/query_order_pay_status",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session,Long orderNo){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<Boolean> response=iOrderService.queryOrderPayStatusIsSuccess(orderNo, user.getId());
		if(response.isSuccess()){
			return ServerResponse.createBySuccessMessage(true);
		}else{
			return ServerResponse.createBySuccessMessage(false);
		}				
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private Map<String,String> paramInit(Map<String,String[]> requestParams) {		
		Map<String,String> params=new HashMap<String,String>();
		Iterator<String> it=requestParams.keySet().iterator();
		while(it.hasNext()){
			String name=it.next();
			String[] valus=requestParams.get(name);
			for(int i=0;i<=valus.length;i++){
				StringBuilder sb=new StringBuilder();
				if(i!=valus.length&&i<valus.length){
					sb.append(valus[i]).append(",");
				}
				if(i==valus.length&&i<valus.length){
					sb.append(valus[i]);
				}
				String valueStr=sb.toString();
				params.put(name, valueStr);
			}			
		}
		return params;
	}
	
}
