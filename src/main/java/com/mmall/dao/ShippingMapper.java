package com.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.Shipping;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);
    
    int deleteBySidAndUid(@Param("shippingId")Integer shippingId,@Param("userId")Integer userId);
    
    int updateByShipping(Shipping record);
    
    Shipping selectBySidAndUid(@Param("shippingId")Integer shippingId,@Param("userId")Integer userId);
    
    List<Shipping> selectByUid(Integer userId);
}