package com.mmall.controller.backend;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileServiceFTP;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.FtpUtil;
import com.mmall.util.PropertiesUtil_mmall;
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
	@Autowired
	private IFileServiceFTP iFileService;
	
	
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
	/**
	 * 
	 * @Title: getList
	 * @Description: TODO  获得商品分页的列表
	 * @param @param session
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return    
	 * @return ServerResponse<PageInfo>    
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/list",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<PageInfo> list(HttpSession session,@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,@RequestParam(value="pageSize",defaultValue="10")Integer pageSize){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员当前处于未登录状态，请登录系统！");
		}
		//校验是否是管理员
		if(iuserService.checkAdminRole(user).isSuccess()){//该用户是管理员
			ServerResponse<PageInfo> response=iProductService.getProductList(pageNum, pageSize);
			return response;
		}else{
			return ServerResponse.createByErrorMessage("该用户非管理员，无权限进行操作！");
		}		
	}
	/**
	 * 
	 * @Title: productSearch
	 * @Description: TODO 根据产品名称或者产品id进行搜索【分页显示】
	 * @param @param session
	 * @param @param productName
	 * @param @param productId
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return    
	 * @return ServerResponse<PageInfo>    
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/search",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<PageInfo> productSearch(HttpSession session,String productName,Integer productId,@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,@RequestParam(value="pageSize",defaultValue="10")Integer pageSize){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员当前处于未登录状态，请登录系统！");
		}
		//校验是否是管理员
		if(iuserService.checkAdminRole(user).isSuccess()){//该用户是管理员
			ServerResponse<PageInfo> response=iProductService.searchProduct(productName, productId, pageNum, pageSize);
			return response;
		}else{
			return ServerResponse.createByErrorMessage("该用户非管理员，无权限进行操作！");
		}		
	}
	/**
	 * 
	 * @Title: upload
	 * @Description: TODO  文件上传
	 * @param @param session
	 * @param @param file
	 * @param @return    
	 * @return ServerResponse<Map<String,String>>    
	 * @throws
	 */
	@RequestMapping(value="/upload",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Map<String,String>> upload(HttpSession session,@RequestParam(value="file",required=false)MultipartFile file){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员当前处于未登录状态，请登录系统！");
		}
		//校验是否是管理员
		if(iuserService.checkAdminRole(user).isSuccess()){//该用户是管理员
			//获得本地路径
			String path=session.getServletContext().getRealPath("upload");
			//1、初始化ftp需要的连接参数
		    String ftpIp=PropertiesUtil_mmall.getProperty("ftp.server.ip");
		    int port=21;
		    String ftpUserName=PropertiesUtil_mmall.getProperty("ftp.image_user");
		    String ftpUserPassword=PropertiesUtil_mmall.getProperty("ftp.image_password");
			//开始上传			
			String uploadFileName=iFileService.upload(path,file,new FtpUtil(ftpIp, port, ftpUserName, ftpUserPassword));
			if(StringUtils.isBlank(uploadFileName)){
				return ServerResponse.createByErrorMessage("上传文件失败！");
			}
			String url=PropertiesUtil_mmall.getProperty("ftp.server.http.prefix")+uploadFileName;
			Map<String,String> fileMap=new HashMap<String,String>();
			fileMap.put("uri", uploadFileName);
			fileMap.put("url", url);			
			return ServerResponse.createBySuccessMessage("上传成功,返回路径",fileMap);
		}else{
			return ServerResponse.createByErrorMessage("该用户非管理员，无权限进行操作！");
		}		
	}
	/**
	 * 
	 * @Title: richtextImgUpload
	 * @Description: TODO 富文本上传 
	 * @param @param session
	 * @param @param file
	 * @param @return    返回的格式是特定的 map
	 * @return Map<String,Object>    
	 * @throws
	 */
	@RequestMapping(value="/richtext_img_upload",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> richtextImgUpload(HttpSession session,@RequestParam(value="file",required=false)MultipartFile file,HttpServletResponse response){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		Map<String,Object> resultMap=new HashMap<String,Object>();
		if(user==null){
			resultMap.put("success", false);
			resultMap.put("msg", "当前管理员未登录");			
			return resultMap;
		}
		//校验是否是管理员
		if(iuserService.checkAdminRole(user).isSuccess()){//该用户是管理员
			//获得本地路径
			String path=session.getServletContext().getRealPath("upload");
			//1、初始化ftp需要的连接参数
		    String ftpIp=PropertiesUtil_mmall.getProperty("ftp.server.ip");
		    int port=21;
		    String ftpUserName=PropertiesUtil_mmall.getProperty("ftp.image_user");
		    String ftpUserPassword=PropertiesUtil_mmall.getProperty("ftp.image_password");
			//开始上传
			String uploadFileName=iFileService.upload(path,file,new FtpUtil(ftpIp, port, ftpUserName, ftpUserPassword));	
			if(StringUtils.isBlank(uploadFileName)){
				resultMap.put("success", false);
				resultMap.put("msg", "上传富文本文件失败！");			
				return resultMap;
			}
			String url=PropertiesUtil_mmall.getProperty("ftp.server.http.prefix")+uploadFileName;
			resultMap.put("success", true);
			resultMap.put("msg", "上传富文本文件成功！");	
			resultMap.put("file_path", url);
			response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
			return resultMap;
		}else{
			resultMap.put("success", false);
			resultMap.put("msg", "当前用户非管理员，无权限操作!");			
			return resultMap;
		}		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
