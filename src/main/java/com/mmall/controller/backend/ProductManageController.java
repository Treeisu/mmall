package com.mmall.controller.backend;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.vo.ProductDetailVo;



/**
 * 
 * @ClassName: ProductManageController
 * @Description: TODO 商品管理
 * @author Barry
 * @date 2017年11月10日 上午10:15:52
 *
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
	
	@Autowired
	private IUserService iuserService;
	@Autowired
	private IProductService iProductService;
	/**
	 * 
	 * @Title: productSaveOrUpdate
	 * @Description: TODO 对产品信息进行保存或更新
	 * @param @param session
	 * @param @param product
	 * @param @return    
	 * @return ServerResponse<Product>    
	 * @throws
	 */
	@RequestMapping(value="/save_or_update",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Product> productSaveOrUpdate(HttpSession session,Product product){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员当前处于未登录状态，请登录系统！");
		}
		//校验是否是管理员
		if(iuserService.checkAdminRole(user).isSuccess()){//该用户是管理员
			ServerResponse<Product> response=iProductService.saveOrUpdateProduct(product);
			return response;
		}else{
			return ServerResponse.createByErrorMessage("该用户非管理员，无权限进行操作！");
		}		
	}
	/**
	 * 
	 * @Title: setSaleStatus
	 * @Description: TODO 设置产品的状态【是否在售】
	 * @param @param session
	 * @param @param productId
	 * @param @param status
	 * @param @return    
	 * @return ServerResponse<String>    
	 * @throws
	 */
	@RequestMapping(value="/set_sale_status",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> setSaleStatus(HttpSession session,Integer productId,Integer status){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员当前处于未登录状态，请登录系统！");
		}
		//校验是否是管理员
		if(iuserService.checkAdminRole(user).isSuccess()){//该用户是管理员
			ServerResponse<String> response=iProductService.setSaleStatus(productId, status);
			return response;
		}else{
			return ServerResponse.createByErrorMessage("该用户非管理员，无权限进行操作！");
		}		
	}
	/**
	 * 
	 * @Title: detail
	 * @Description: TODO 后台 获得产品的详细信息
	 * @param @param session
	 * @param @param productId
	 * @param @return    
	 * @return ServerResponse<ProductDetailVo>    
	 * @throws
	 */
	@RequestMapping(value="/detail",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<ProductDetailVo> detail(HttpSession session,Integer productId){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员当前处于未登录状态，请登录系统！");
		}
		//校验是否是管理员
		if(iuserService.checkAdminRole(user).isSuccess()){//该用户是管理员
			ServerResponse<ProductDetailVo> response=iProductService.manageProductDetail(productId);
			return response;
		}else{
			return ServerResponse.createByErrorMessage("该用户非管理员，无权限进行操作！");
		}		
	}
	
	@RequestMapping(value="/list",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<ProductDetailVo> getList(HttpSession session,@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,@RequestParam(value="pageSize",defaultValue="0")Integer pageSize){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员当前处于未登录状态，请登录系统！");
		}
		//校验是否是管理员
		if(iuserService.checkAdminRole(user).isSuccess()){//该用户是管理员
			ServerResponse<ProductDetailVo> response=null;
			return response;
		}else{
			return ServerResponse.createByErrorMessage("该用户非管理员，无权限进行操作！");
		}		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
