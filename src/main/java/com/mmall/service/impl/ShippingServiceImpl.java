package com.mmall.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
/**
 * 
 * @ClassName: ShippingServiceImpl
 * @Description: TODO 地址管理 以下所有的方法都传入了userId【从session中获得】进行判断，可以防止横向越权
 * @author Barry
 * @date 2017年11月20日 下午7:50:12
 *
 */
@Service
public class ShippingServiceImpl implements IShippingService {
	@Autowired
	private ShippingMapper shippingMapper;
	/**
	 * 添加地址
	 */
	@Override
	public ServerResponse<Map<String,Integer>> add(Integer userId, Shipping shipping) {
		// TODO Auto-generated method stub
		shipping.setUserId(userId);
		int rowCount=shippingMapper.insert(shipping);//影响行数
		if(rowCount<=0){
			return ServerResponse.createByErrorMessage("数据库写入错误！");
		}
		Map<String, Integer> map=new HashMap<String, Integer>();
		map.put("shippingId", shipping.getId());//这里可以拿到id是因为mapper.xml中配置了返回
		return ServerResponse.createBySuccessMessage("数据库写入成功！返回该记录主键id", map);
	}
	/**
	 * 删除地址
	 */
	@Override
	public ServerResponse<String> del(Integer userId, Integer shippingId) {
		// TODO Auto-generated method stub
		//这里删除带上userId参数是为了防止横向越权 ，删除非自己的数据
		int rowCount=shippingMapper.deleteBySidAndUid(shippingId, userId);
		if(rowCount<=0){
			return ServerResponse.createByErrorMessage("数据库写入失败！");
		}
		return ServerResponse.createBySuccessMessage("删除收货地址成功！");
	}
	/**
	 * 更新地址
	 */
	@Override
	public ServerResponse<String> update(Integer userId, Shipping shipping) {
		// TODO Auto-generated method stub
		shipping.setUserId(userId);
		int rowCount=shippingMapper.updateByShipping(shipping);
		if(rowCount<=0){
			return ServerResponse.createByErrorMessage("数据库写入错误！");
		}
		return ServerResponse.createBySuccessMessage("收货地址更新成功！");
	}
	/**
	 * 获得单条地址
	 */
	@Override
	public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
		// TODO Auto-generated method stub
		Shipping shipping=shippingMapper.selectBySidAndUid(shippingId, userId);
		if(shipping==null){
			return ServerResponse.createByErrorMessage("无法查询到该地址信息！");
		}
		return ServerResponse.createBySuccessMessage("查询收货地址成功！",shipping);
	}
	/**
	 * 获得所有地址【分页】
	 */
	@Override
	public ServerResponse<PageInfo<Shipping>> list(Integer userId, Integer pageNum, Integer pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		List<Shipping> list=shippingMapper.selectByUid(userId);
		PageInfo<Shipping> pageInfo=new PageInfo<>(list);
		return ServerResponse.createBySuccessMessage("查询收货地址列表成功", pageInfo);
	}

}
