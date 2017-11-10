package com.mmall.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;

public class ProductServiceImpl implements IProductService{
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private CategoryMapper categoryMapper;
	
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
			return ServerResponse.createByErrorMessage("该商品已下架或不存在！");
		}
		//封装VO对象
		ProductDetailVo productDetailVo=this.assembleProductDetailVo(product);
		return ServerResponse.createBySuccessMessage("获取产品信息成功", productDetailVo);
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ServerResponse getProductList(Integer pageNum,Integer pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum,pageSize);//设置起始页和页面大小
		//开始查询数据库中 所有的数据
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
		return ServerResponse.createBySuccessMessage("获取商品列表成功", pageInfo);
	}
	
	
	
	
	
	
	
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
}
