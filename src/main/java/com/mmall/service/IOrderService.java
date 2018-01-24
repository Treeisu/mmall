package com.mmall.service;


import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;



@SuppressWarnings("rawtypes")
public interface IOrderService {
	ServerResponse<Map<String, String>> pay(Long orderNo,Integer userId,String path);
	ServerResponse<Boolean> queryOrderPayStatusIsSuccess(Long orderNo,Integer userId);
	ServerResponse<String> aliCallBackCheck(Map<String,String[]> params);
	ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId) throws Exception;
	ServerResponse<String> cancel(Integer userId, Long orderNo);
	ServerResponse<String> notShow(Integer userId, Long orderNo);
	ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId);
	ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);
	ServerResponse<OrderVo> manageGetOrderDetail(Long orderNo);
	ServerResponse<PageInfo> getOrderList(Integer userId,Integer pageNum,Integer pageSize);
	ServerResponse<PageInfo> manageGetOrderList(Integer pageNum,Integer pageSize) ;	
	ServerResponse<PageInfo> manageSearch(Integer pageNum,Integer pageSize,Long orderNo) ;
	ServerResponse<String> sendGoods(Long orderNo);
}
