package com.mmall.service;


import java.util.List;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

public interface ICategroyService {
	ServerResponse<String> addCategory(String categoryName,Integer parentId);
	ServerResponse<Category> setCategoryName(Integer categoryId,String categoryName);
	ServerResponse<List<Category>> selectChildrenCategory(Integer categoryId);
	ServerResponse<List<Integer>> selectChildrensAllCategory(Integer categoryId);
}
