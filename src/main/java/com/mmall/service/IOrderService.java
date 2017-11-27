package com.mmall.service;

import java.util.Map;

import com.mmall.common.ServerResponse;

public interface IOrderService {
	ServerResponse<Map<String, String>> pay(Long orderNo,Integer userId,String path);
	ServerResponse<Boolean> queryOrderPayStatusIsSuccess(Long orderNo,Integer userId);
	ServerResponse<String> aliCallBackCheck(Map<String,String[]> params);
}
