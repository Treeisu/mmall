package com.mmall.dao;

import java.util.List;

import com.mmall.pojo.Category;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
    /**
     * 获得一级子节点的信息
     * @Title: selectChildrenCategory
     * @Description: TODO
     * @param @param parentId
     * @param @return    
     * @return ServerResponse<List<Category>>    
     * @throws
     */
    List<Category> selectChildrenCategory(Integer parentId);
}