package com.mmall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;

/**
 * 
 * @ClassName: ProductController
 * @Description: TODO 前台客服获得产品的接口
 * @author Barry
 * @date 2017年11月13日 下午4:04:35
 *
 */
@Controller
@RequestMapping("/product")
public class ProductController {
	@Autowired
	private IProductService iProductService;
	
	/**
	 * 
	 * @Title: detail
	 * @Description: TODO 获得产品详情
	 * @param @param productId
	 * @param @return    
	 * @return ServerResponse<ProductDetailVo>    
	 * @throws
	 */
	@RequestMapping(value="/detail",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<ProductDetailVo> detail(Integer productId){
		ServerResponse<ProductDetailVo> response=iProductService.getProductDetail(productId);
		return response;		
	}
	/**
	 * 
	 * @Title: getList
	 * @Description: TODO 产品分类列表以及搜索
	 * @param @param keyword
	 * @param @param pageNum
	 * @param @param pageSize
	 * @param @return    
	 * @return ServerResponse<PageInfo>    
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/list",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<PageInfo> getList(@RequestParam(value="keyword",required=false)String keyword,
											@RequestParam(value="catagoryId",required=false)Integer catagoryId,
											@RequestParam(value="pageNum",defaultValue="1")Integer pageNum,
											@RequestParam(value="pageSize",defaultValue="10")Integer pageSize,
											@RequestParam(value="orderBy",defaultValue="")String orderBy){
		ServerResponse<PageInfo> response=iProductService.getProductByKeyword(keyword, catagoryId, pageNum, pageSize, orderBy);
		return response;
				
	}
	
}
