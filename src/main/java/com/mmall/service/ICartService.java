package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

public interface ICartService {
	ServerResponse<CartVo> list(Integer userId);
	ServerResponse<CartVo> add(Integer userId, Integer count, Integer productId);
	ServerResponse<CartVo> update(Integer userId, Integer count, Integer productId);
	ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);
	ServerResponse<CartVo> update_selectOrUnselectAll(Integer userId,Integer checkedStatus);
	ServerResponse<CartVo> update_selectOrUnselect(Integer userId,Integer checkedStatus,Integer productId);
	ServerResponse<Integer> getCartProductCount(Integer userId);
}
