package com.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.Product;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKeyWithBLOBs(Product record);

    int updateByPrimaryKey(Product record);
    
    List<Product> selectList();
    
    List<Product> selectByNameOrProductId(@Param("productName")String productName,@Param("productId")Integer productId);
    
    List<Product> selectByNameOrCategoryIds(@Param("productName")String productName,@Param("categoryIds")List<Integer> categoryIds);
    
    int reduceStockByPid(@Param("productId")Integer productId,@Param("quantity")Integer quantity);
    
    int addStockByPid(@Param("productId")Integer productId,@Param("quantity")Integer quantity);
}