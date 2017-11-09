package com.mmall.controller.backend;


import java.util.List;

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
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategroyService;
import com.mmall.service.IUserService;


/**
 * 
 * @ClassName: CategoryManageController
 * @Description: TODO 后台管理员进行商品类目管理
 * @author Barry
 * @date 2017年11月9日 上午10:24:04
 *
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
	@Autowired
	private IUserService iuserService;
	@Autowired
	private ICategroyService iCategroyService;
	
	/**
	 * 
	 * @Title: addCategory
	 * @Description: TODO  添加商品类目
	 * @param @param session
	 * @param @param categoryName
	 * @param @param parentId
	 * @param @return    
	 * @return ServerResponse<String>    
	 * @throws
	 */
	@RequestMapping(value="/add_category",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> addCategory(HttpSession session,String categoryName,@RequestParam(value="parentId",defaultValue="0")Integer parentId){
		//@RequstParam()表示对某个参数进行限制，如果未传此参数则赋予默认值
		//校验是否登陆
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员当前处于未登录状态，请登录系统！");
		}
		//校验是否是管理员
		if(iuserService.checkAdmin(user).isSuccess()){//该用户是管理员
			ServerResponse<String> response=iCategroyService.addCategory(categoryName, parentId);
			return response;
		}else{
			return ServerResponse.createByErrorMessage("该用户非管理员，无权限进行操作！");
		}
						
	}
	
	/**
	 * 
	 * @Title: setCategoryName
	 * @Description: TODO  更新商品类目的名字
	 * @param @param session
	 * @param @param categoryId
	 * @param @param categoryName
	 * @param @return    
	 * @return ServerResponse<Category>    
	 * @throws
	 */
	@RequestMapping(value="/set_category_name",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<Category> setCategoryName(HttpSession session,Integer categoryId,String categoryName){
		//@RequstParam()表示对某个参数进行限制，如果未传此参数则赋予默认值
		//校验是否登陆
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员当前处于未登录状态，请登录系统！");
		}
		//校验是否是管理员
		if(iuserService.checkAdmin(user).isSuccess()){//该用户是管理员
			ServerResponse<Category> response=iCategroyService.setCategoryName(categoryId, categoryName);
			return response;
		}else{
			return ServerResponse.createByErrorMessage("该用户非管理员，无权限进行操作！");
		}						
	}
	/**
	 * 
	 * @Title: getChildrenCategory
	 * @Description: TODO 查询某个商品分类id下的       一级子节点信息
	 * @param @param session
	 * @param @param categoryId
	 * @param @return    
	 * @return ServerResponse<List<Category>>    返回所有一级子节点信息
	 * @throws
	 */
	@RequestMapping(value="/get_children_category",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<List<Category>> getChildrenCategory(HttpSession session,@RequestParam(value="categoryId",defaultValue="0")Integer categoryId){
		//@RequstParam()表示对某个参数进行限制，如果未传此参数则赋予默认值
		//校验是否登陆
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员当前处于未登录状态，请登录系统！");
		}
		//校验是否是管理员
		if(iuserService.checkAdmin(user).isSuccess()){//该用户是管理员
			ServerResponse<List<Category>> response=iCategroyService.selectChildrenCategory(categoryId);
			return response;
		}else{
			return ServerResponse.createByErrorMessage("该用户非管理员，无权限进行操作！");
		}						
	}
	/**
	 * 
	 * @Title: getSelfChildrensCategory
	 * @Description: TODO 获得当前节点下      所有子节点ids
	 * @param @param session
	 * @param @param categoryId
	 * @param @return    
	 * @return ServerResponse<List<Integer>>    当前节点id以及所有子节点ids
	 * @throws
	 */
	@RequestMapping(value="/get_childrens_all_category",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<List<Integer>> getChildrensAllCategory(HttpSession session,@RequestParam(value="categoryId",defaultValue="0")Integer categoryId){
		//@RequstParam()表示对某个参数进行限制，如果未传此参数则赋予默认值
		//校验是否登陆
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员当前处于未登录状态，请登录系统！");
		}
		//校验是否是管理员
		if(iuserService.checkAdmin(user).isSuccess()){//该用户是管理员
			ServerResponse<List<Integer>> response=iCategroyService.selectChildrensAllCategory(categoryId);
			return response;
		}else{
			return ServerResponse.createByErrorMessage("该用户非管理员，无权限进行操作！");
		}						
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
