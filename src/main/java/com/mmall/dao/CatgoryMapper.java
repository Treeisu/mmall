package com.mmall.dao;

import com.mmall.pojo.Catgory;

public interface CatgoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Catgory record);

    int insertSelective(Catgory record);

    Catgory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Catgory record);

    int updateByPrimaryKey(Catgory record);
}