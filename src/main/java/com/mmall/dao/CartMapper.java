package com.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.Cart;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);
    
    Cart selectByUidAndPid(@Param("userId")Integer userId,@Param("productId")Integer productId);
    
    List<Cart> selectByUid(Integer userId);
    
    int selectCheckedStatusByUid(Integer userId);
    
    int deleteByUidPIDlist(@Param("userId")Integer userId,@Param("productIdList")List<String> productIdList);
    
    int selectOrUnselectAll(@Param("userId")Integer userId,@Param("checked")Integer checked);
    
    int selectOrUnselect(@Param("userId")Integer userId,@Param("checked")Integer checked,@Param("productId")Integer productId);
    
    int selectCartProductCount(Integer userId);
}