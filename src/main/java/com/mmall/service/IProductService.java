package com.mmall.service;

import java.util.List;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {
	ServerResponse<Product> saveOrUpdateProduct(Product product);
	ServerResponse<String> setSaleStatus(Integer productId,Integer status);
	ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
	ServerResponse<List<ProductDetailVo>> getProductList(Integer pageNum,Integer pageSize);
}
