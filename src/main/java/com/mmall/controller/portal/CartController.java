package com.mmall.controller.portal;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;

/**
 * 
 * @ClassName: CartController
 * @Description: TODO 客户购物车相关请求
 * @author Barry
 * @date 2017年11月15日 上午9:55:21
 *
 */
@Controller
@RequestMapping("/cart")
public class CartController {
	@Autowired
	private ICartService iCartService;
	
	/**
	 * 
	 * @Title: list
	 * @Description: TODO 获得购物车列表
	 * @param @param session
	 * @param @return    
	 * @return ServerResponse<CartVo>    
	 * @throws
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<CartVo> list(HttpSession session){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<CartVo> response=iCartService.list(user.getId());		
		return response;		
	}
	/**
	 * 	
	 * @Title: add
	 * @Description: TODO 添加购物车
	 * @param @param session
	 * @param @param count 需要购买的数量
	 * @param @param productId
	 * @param @return    
	 * @return ServerResponse<CartVo>    
	 * @throws
	 */
	@RequestMapping(value="/add",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<CartVo> add(HttpSession session,Integer count,Integer productId){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<CartVo> response=iCartService.add(user.getId(), count, productId);		
		return response;		
	}
	/**
	 * 	
	 * @Title: update
	 * @Description: TODO 更新购物车
	 * @param @param session
	 * @param @param num 购物车中该商品的总数量
	 * @param @param productId
	 * @param @return    
	 * @return ServerResponse<CartVo>    
	 * @throws
	 */
	@RequestMapping(value="/update",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<CartVo> update(HttpSession session,Integer num,Integer productId){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<CartVo> response=iCartService.update(user.getId(), num, productId);		
		return response;		
	}
	/**
	 * 
	 * @Title: deleteProduct
	 * @Description: TODO 删除购物车中的产品
	 * @param @param session
	 * @param @param productIds 可能是一下删除多个商品，所以前端传来一个字符串用逗号分隔
	 * @param @return    
	 * @return ServerResponse<CartVo>    
	 * @throws
	 */
	@RequestMapping(value="/delete_product",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<CartVo> deleteProduct(HttpSession session,String productIds){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<CartVo> response=iCartService.deleteProduct(user.getId(), productIds);		
		return response;		
	}
	/**
	 * 
	 * @Title: selectAll
	 * @Description: TODO 全部选中产品
	 * @param @param session
	 * @param @return    
	 * @return ServerResponse<CartVo>    
	 * @throws
	 */
	@RequestMapping(value="/select_all",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<CartVo> selectAll(HttpSession session){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<CartVo> response=iCartService.selectOrUnselectAll(user.getId(), Const.Cart.CHECKED);		
		return response;		
	}
	/**
	 * 
	 * @Title: UnSelectAll
	 * @Description: TODO 全部取消选中
	 * @param @param session
	 * @param @return    
	 * @return ServerResponse<CartVo>    
	 * @throws
	 */
	@RequestMapping(value="/un_select_all",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<CartVo> UnSelectAll(HttpSession session){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<CartVo> response=iCartService.selectOrUnselectAll(user.getId(), Const.Cart.UN_CHECKED);		
		return response;		
	}	
	/**
	 * 
	 * @Title: select
	 * @Description: TODO 单个选中
	 * @param @param session
	 * @param @param productId
	 * @param @return    
	 * @return ServerResponse<CartVo>    
	 * @throws
	 */
	@RequestMapping(value="/select",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<CartVo> select(HttpSession session,Integer productId){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<CartVo> response=iCartService.selectOrUnselect(user.getId(), Const.Cart.CHECKED,productId);		
		return response;		
	}
	/**
	 * 
	 * @Title: UnSelect
	 * @Description: TODO 单个取消
	 * @param @param session
	 * @param @param productId
	 * @param @return    
	 * @return ServerResponse<CartVo>    
	 * @throws
	 */
	@RequestMapping(value="/un_select",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<CartVo> UnSelect(HttpSession session,Integer productId){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"当前处于未登录状态，请登录系统！");
		}		
		ServerResponse<CartVo> response=iCartService.selectOrUnselect(user.getId(), Const.Cart.UN_CHECKED,productId);		
		return response;		
	}
	/**
	 * 
	 * @Title: getCartProductCount
	 * @Description: TODO 获得购物车中商品的个数
	 * @param @param session
	 * @param @return    
	 * @return ServerResponse<Integer>    
	 * @throws
	 */
	@RequestMapping(value="/get_cart_product_count",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Integer> getCartProductCount(HttpSession session){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createBySuccessMessage("查询购物车成功",0);
		}		
		ServerResponse<Integer> response=iCartService.getCartProductCount(user.getId());		
		return response;		
	}
}
