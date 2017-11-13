package com.mmall.service;


import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;
@SuppressWarnings("rawtypes")
public interface IProductService {
	ServerResponse<Product> saveOrUpdateProduct(Product product);
	ServerResponse<String> setSaleStatus(Integer productId,Integer status);
	ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
	ServerResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize);	
	ServerResponse<PageInfo> searchProduct(String productName,Integer productId,Integer pageNum,Integer pageSize);
	ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
	ServerResponse<PageInfo> getProductByKeyword(String keyword,Integer catagoryId,Integer pageNum,Integer pageSize,String orderBy);
}
