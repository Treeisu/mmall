package com.mmall.service.impl;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategroyService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
@Service("iProductService")
public class ProductServiceImpl implements IProductService{
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private CategoryMapper categoryMapper;
	@Autowired
	private ICategroyService iCategroyService;
		
	/**
	 * 
	 * @Title: assembleProductDetailVo
	 * @Description: TODO  设置vo类
	 * @param @param product
	 * @param @return    
	 * @return ProductDetailVo    
	 * @throws
	 */
	private ProductDetailVo assembleProductDetailVo(Product product) {
		ProductDetailVo productDetailVo=new ProductDetailVo();
		productDetailVo.setId(product.getId());
		productDetailVo.setSubtitle(product.getSubtitle());
		productDetailVo.setPrice(product.getPrice());
		productDetailVo.setMainImage(productDetailVo.getMainImage());
		productDetailVo.setSubImages(product.getSubImages());
		productDetailVo.setCategoryId(product.getCategoryId());
		productDetailVo.setDetail(product.getDetail());
		productDetailVo.setName(product.getName());
		productDetailVo.setStatus(product.getStatus());
		productDetailVo.setStock(product.getStock());
		//imageHost  读取配置文件获得
		productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://www.jiangbiao.xyz/"));
		//获得父级类目id
		Integer parentCategoryId=categoryMapper.selectByPrimaryKey(product.getCategoryId()).getParentId();
		productDetailVo.setParentCategoryId(parentCategoryId);
		productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
		productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
		return productDetailVo;
	}
	/**
	 * 
	 * @Title: assembleProductListVo
	 * @Description: TODO 设置vo类
	 * @param @param product
	 * @param @return    
	 * @return ProductListVo    
	 * @throws
	 */
	private ProductListVo assembleProductListVo(Product product) {
		ProductListVo productListVo=new ProductListVo();
		productListVo.setId(product.getId());
		productListVo.setSubtitle(product.getSubtitle());
		productListVo.setPrice(product.getPrice());
		productListVo.setMainImage(productListVo.getMainImage());
		productListVo.setCategoryId(product.getCategoryId());
		productListVo.setName(product.getName());
		productListVo.setStatus(product.getStatus());
		//imageHost  读取配置文件获得
		productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://www.jiangbiao.xyz/"));	
		return productListVo;
	}
	
	
	
	
	/**
	 * 保存或者更新
	 */
	@Override
	public ServerResponse<Product> saveOrUpdateProduct(Product product) {
		// TODO Auto-generated method stub
		//判断是否为空
		if(product==null){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"产品修改发生错误：参数为空");
		}else{			
			//判断子图是否为空,并设置主图显示
			if(StringUtils.isNotBlank(product.getSubImages())){
				//获得所有子图【数组】
				String[] imgArrs=product.getSubImages().split(",");
				//将第一张子图赋给主图显示
				if(imgArrs.length>0){
					product.setMainImage(imgArrs[0]);
				}			
			}
			//开始保or更新操作
			//判断id是否为空【更新or保存：为null就是保存】
			if(product.getId()!=null){
				int resultCount=productMapper.updateByPrimaryKey(product);
				if(resultCount>0){
					return ServerResponse.createBySuccessMessage("产品信息更新成功！", product);
				}else{
					return ServerResponse.createByErrorMessage("产品信息更新异常：数据库写入失败");
				}
			}else{
				int resultCount=productMapper.insert(product);
				if(resultCount>0){
					return ServerResponse.createBySuccessMessage("产品信息保存成功！", product);
				}else{
					return ServerResponse.createByErrorMessage("产品信息保存异常：数据库写入失败");
				}
			}
		}					
	}
	/**
	 * 设置产品的状态
	 */
	@Override
	public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
		// TODO Auto-generated method stub
		//校验参数是否为空
		if(productId==null||status==null){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "状态设置失败：参数错误");
		}
		//创建对象
		Product product=new Product();
		product.setId(productId);
		product.setStatus(status);
		int resultCount=productMapper.updateByPrimaryKeySelective(product);
		if(resultCount>0){
			return ServerResponse.createBySuccessMessage("产品状态设置成功");
		}else{
			return ServerResponse.createByErrorMessage("产品状态设置异常：数据库写入失败");
		}
	}
	/**
	 * 获得产品详细的信息
	 */
	@Override
	public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
		// TODO Auto-generated method stub
		//校验参数是否为空
		if(productId==null){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "获取产品信息失败：参数错误");
		}
		//查询对象
		Product product=productMapper.selectByPrimaryKey(productId);
		if(product==null){
			return ServerResponse.createByErrorMessage("该商品不存在！");
		}
		//封装VO对象
		ProductDetailVo productDetailVo=this.assembleProductDetailVo(product);
		return ServerResponse.createBySuccessMessage("获取产品信息成功", productDetailVo);
	}		
	/**
	 * 获取产品列表
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ServerResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize) {
		// TODO Auto-generated method stub		
		//设置起始页和页面大小【配置此项则会在执行的sql语句末尾添加上limit限制】
		PageHelper.startPage(pageNum,pageSize);
		//连接数据库开始查询   所有的数据
		List<Product> list=productMapper.selectList();		
		//封装返回的pageInfo
		PageInfo pageInfo=new PageInfo(list);
		//封装pageInfo里面需要显示的vo的list列表
		List<ProductListVo> listVos=new ArrayList<ProductListVo>();
		for(Product p:list){
			ProductListVo productListVo=this.assembleProductListVo(p);
			listVos.add(productListVo);
		}				
		pageInfo.setList(listVos);
		return ServerResponse.createBySuccessMessage("获取商品成功", pageInfo);
	}
	/**
	 * 搜索产品
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, Integer pageNum,Integer pageSize) {
		// TODO Auto-generated method stub
		//设置起始页和页面大小【配置此项则会在执行的sql语句末尾添加上limit限制】
		PageHelper.startPage(pageNum,pageSize);
		//设置模糊查询参数
		if(StringUtils.isNotBlank(productName)){
			productName=new StringBuilder().append("%").append(productName).append("%").toString();
		}
		//查询数据库
		List<Product> list=productMapper.selectByNameOrProductId(productName, productId);
		//封装返回的pageInfo
		PageInfo pageInfo=new PageInfo(list);
		//封装pageInfo里面需要显示的vo的list列表
		List<ProductListVo> listVos=new ArrayList<ProductListVo>();
		for(Product p:list){
			ProductListVo productListVo=this.assembleProductListVo(p);
			listVos.add(productListVo);
		}				
		pageInfo.setList(listVos);
		return ServerResponse.createBySuccessMessage("搜索商品成功", pageInfo);
	}
	/**
	 * 前台客户获得产品信息
	 */
	@Override
	public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
		// TODO Auto-generated method stub
		//校验参数是否为空
		if(productId==null){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "获取产品信息失败：参数错误");
		}
		//查询对象
		Product product=productMapper.selectByPrimaryKey(productId);
		if(product==null){
			return ServerResponse.createByErrorMessage("该商品不存在！");
		}else{
			if(product.getStatus()!=Const.ProductStatus.ON_SALE.getCode()){
				return ServerResponse.createByErrorMessage("该商品已下架!");
			}
			//封装VO对象
			ProductDetailVo productDetailVo=this.assembleProductDetailVo(product);
			return ServerResponse.createBySuccessMessage("获取产品信息成功", productDetailVo);
		}				
	}
	/**
	 * 前台用户分类列表
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ServerResponse<PageInfo> getProductByKeyword(String keyword, Integer categoryId, Integer pageNum,Integer pageSize,String orderBy) {
		// TODO Auto-generated method stub
		//校验参数
		if(StringUtils.isBlank(keyword)&&categoryId==null){
			ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数有误！");
		}
		//分类ids【递归查询得到】
		List<Integer> ids=null;
		//校验分类id 获得分类ids
		if(categoryId!=null){
			Category category=categoryMapper.selectByPrimaryKey(categoryId);
			//查询不到数据，并且没有搜索条件，则返回空集合
			if(category==null&&StringUtils.isBlank(keyword)){
				PageHelper.startPage(pageNum,pageSize);
				List<ProductDetailVo> list=new ArrayList<ProductDetailVo>();
				PageInfo pageInfo=new PageInfo(list);
				return ServerResponse.createBySuccessMessage("请求成功", pageInfo);
			}	
			//调用递归查询  返回分类ids
			ids=iCategroyService.selectChildrensAllCategory(categoryId).getDate();
		}
		//拼接参数
		if(StringUtils.isNotBlank(keyword)){
			keyword=new StringBuffer().append("%").append(keyword).append("%").toString();			
		}
		PageHelper.startPage(pageNum,pageSize);
		//动态排序
		if(StringUtils.isNotBlank(orderBy)){
			if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains("orderBy")){//按照价格升序降序
				String[] arrs=orderBy.split("_");
				PageHelper.orderBy(arrs[0]+" "+arrs[1]);//格式 price desc				
			}
		}
		//开始查询
		List<Product> list=productMapper.selectByNameOrCategoryIds(StringUtils.isBlank(keyword)?null:keyword,ids.size()==0?null:ids);
		//封装详细列表
		List<ProductListVo> listVos=new ArrayList<ProductListVo>();
		for(Product p:list){
			ProductListVo pl=assembleProductListVo(p);
			listVos.add(pl);
		}
		PageInfo pageInfo=new PageInfo(list);
		pageInfo.setList(listVos);
		return ServerResponse.createBySuccessMessage("获得产品列表成功", pageInfo);
	}
	
	
	
	
	
	
	
	
}
