package com.mmall.service;


import java.util.Map;

import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

public interface IOrderService {
	ServerResponse<Map<String, String>> pay(Long orderNo,Integer userId,String path);
	ServerResponse<Boolean> queryOrderPayStatusIsSuccess(Long orderNo,Integer userId);
	ServerResponse<String> aliCallBackCheck(Map<String,String[]> params);
	ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId);
}
