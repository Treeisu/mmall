package com.mmall.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategroyService;
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategroyService {	
	private Logger logger=LoggerFactory.getLogger(CategoryServiceImpl.class);
	@Autowired
	private CategoryMapper categoryMapper;
	
	
	@Override
	/**
	 * 添加商品类目
	 */
	public ServerResponse<String> addCategory(String categoryName, Integer parentId) {
		// TODO Auto-generated method stub
		if(StringUtils.isBlank(categoryName)||parentId==null){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "添加商品类目：参数错误！");
		}
		Category category=new Category();
		category.setName(categoryName);
		category.setParentId(parentId);
		category.setStatus(true);//表示当前分类可用
		int rowCount=categoryMapper.insert(category);
		if(rowCount>0){
			return ServerResponse.createBySuccessMessage("商品类目添加成功！");
		}
		return ServerResponse.createByErrorMessage("数据库写入异常，商品类目添加失败！");
	}
	/**
	 * 更改商品类目名
	 */
	@Override
	public ServerResponse<Category> setCategoryName(Integer categoryId, String categoryName) {
		// TODO Auto-generated method stub
		if(StringUtils.isBlank(categoryName)||categoryId==null){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "更新商品类目：参数错误！");
		}
		Category category=new Category();
		category.setId(categoryId);
		category.setName(categoryName);
		int rowCount=categoryMapper.updateByPrimaryKeySelective(category);
		if(rowCount>0){
			category=categoryMapper.selectByPrimaryKey(categoryId);
			return ServerResponse.createBySuccessMessage("商品类目更新成功！",category);
		}
		return ServerResponse.createByErrorMessage("数据库写入异常，商品类目更新失败！");
	}
	/**
	 * 获得一级子节点的所有商品信息
	 */
	@Override
	public ServerResponse<List<Category>> selectChildrenCategory(Integer categoryId) {
		// TODO Auto-generated method stub
		//获得一级子节点
		List<Category> list=categoryMapper.selectChildrenCategory(categoryId);
		if(CollectionUtils.isEmpty(list)){
			logger.info("当前商品类目ID下无子节点信息！");
		}
		return ServerResponse.createBySuccessMessage("找到当前类目ID下子节点信息", list);
	}
	/**
	 * 获得所有子节点的信息
	 */
	@Override
	public ServerResponse<List<Integer>> selectChildrensAllCategory(Integer categoryId) {
		// TODO Auto-generated method stub
		Set<Category> set=new HashSet<Category>();
		//调用递归查询算法
		findAllCategrory(set, categoryId);
		List<Integer> idList=new ArrayList<Integer>();
		if(CollectionUtils.isNotEmpty(set)){
			for(Category c:set){
				idList.add(c.getId());
			}
		}
		return ServerResponse.createBySuccessMessage("查询到数据", idList);
	}
	/**
	 * 
	 * @Title: findAllCategrory
	 * @Description: TODO  查找当前节点以及子节点信息
	 * @param @param set
	 * @param @param parentId
	 * @param @return    
	 * @return Set<Category>    
	 * @throws
	 */
	public Set<Category> findAllCategrory(Set<Category> set,Integer parentId){
		//查找当前id的节点信息
		Category category=categoryMapper.selectByPrimaryKey(parentId);
		//将当前节点放入set集合
		if(category!=null){
			set.add(category);
		}
		//查找当前节点的     一级子节点
		List<Category> list=categoryMapper.selectChildrenCategory(parentId);
		if(CollectionUtils.isNotEmpty(list)){//当节点下无子节点时则会跳出递归循环
			for(Category c:list){
				findAllCategrory(set, c.getId());
			}
		}		
		return set;		
	}	
}
