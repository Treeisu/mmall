package com.mmall.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;

@Service("iCartService")
public class CartServiceImpl implements ICartService{
	@Autowired
	private CartMapper cartMapper;
	@Autowired
	private ProductMapper productMapper;
	
	
	/**
	 * 获得购物车
	 */
	@Override
	public ServerResponse<CartVo> list(Integer userId) {
		// TODO Auto-generated method stub
		CartVo cartVo=this.getCartVoLimit(userId);		
		return ServerResponse.createBySuccessMessage("查询购物车成功", cartVo);
	}
	/**
	 * 商品加入购物车
	 */
	@Override
	public ServerResponse<CartVo> add(Integer userId, Integer count, Integer productId) {
		// TODO Auto-generated method stub
		//查询用户是否已添加过此商品进购物车
		//校验参数		
		if(productId==null||count==null){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误！");
		}else{
			Product product=new Product();
			product=productMapper.selectByPrimaryKey(productId);
			if(product==null){
				return ServerResponse.createByErrorMessage("添加失败，该商品不存在！");
			}else{				
				Cart cart=cartMapper.selectByUidAndPid(userId, productId);
				if(cart!=null){//购物车中已存在过此商品					
					cart.setQuantity(cart.getQuantity()+count);					
					cartMapper.updateByPrimaryKeySelective(cart);//更新
				}else{//未添加过，则新建一个										
					Cart c=new Cart();
					c.setQuantity(count);
					c.setProductId(productId);
					c.setUserId(userId);
					c.setChecked(Const.Cart.CHECKED);					
					cartMapper.insert(c);//保存
				}
				//添加完成 查询购物车
				CartVo cartVo=this.getCartVoLimit(userId);		
				return ServerResponse.createBySuccessMessage("查询购物车成功", cartVo);
			}
		}
		
	}
	/**
	 * 更新购物车的某个商品的购买数量
	 * num指的不是新增个数 某个商品在购物车中的总个数【now】
	 */
	@Override
	public ServerResponse<CartVo> update(Integer userId, Integer num, Integer productId){		
		if(productId==null||num==null){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误！");
		}else{
			Product product=new Product();
			product=productMapper.selectByPrimaryKey(productId);
			if(product==null){
				return ServerResponse.createByErrorMessage("更新失败，该商品不存在！");
			}else{
				Cart cart=cartMapper.selectByUidAndPid(userId, productId);
				if(cart!=null){
					cart.setQuantity(num);
					cartMapper.updateByPrimaryKeySelective(cart);//更新购物车
				}
				//更新完成，再次查询一遍
				CartVo cartVo=this.getCartVoLimit(userId);		
				return ServerResponse.createBySuccessMessage("查询购物车成功", cartVo);
			}			
		}
				
	}
	/**
	 * 删除商品【可能一下删除多个，所以商品id是一个字符串可能包含多个id】
	 */
	@Override
	public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
		// TODO Auto-generated method stub
		List<String> productIdList=Splitter.on(",").splitToList(productIds);
		if(CollectionUtils.isEmpty(productIdList)){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误！");
		}
		//进行删除
		cartMapper.deleteByUidPIDlist(userId, productIdList);
		//删除完成，再次查询一遍
		CartVo cartVo=this.getCartVoLimit(userId);		
		return ServerResponse.createBySuccessMessage("查询购物车成功", cartVo);
	}
	/**
	 * 全选或者全取消
	 */
	@Override
	public ServerResponse<CartVo> selectOrUnselectAll(Integer userId,Integer checkedStatus) {
		// TODO Auto-generated method stub
		cartMapper.selectOrUnselectAll(userId, checkedStatus);
		CartVo cartVo=this.getCartVoLimit(userId);		
		return ServerResponse.createBySuccessMessage("查询购物车成功", cartVo);
	}
	/**
	 * 单个选中或者单个取消
	 */
	@Override
	public ServerResponse<CartVo> selectOrUnselect(Integer userId,Integer checkedStatus,Integer productId) {
		// TODO Auto-generated method stub
		cartMapper.selectOrUnselect(userId, checkedStatus, productId);
		CartVo cartVo=this.getCartVoLimit(userId);		
		return ServerResponse.createBySuccessMessage("查询购物车成功", cartVo);
	}
	/**
	 * 获得购物车中产品的个数
	 */
	@Override
	public ServerResponse<Integer> getCartProductCount(Integer userId) {
		// TODO Auto-generated method stub
		Integer sum=0;
		if(userId!=null){
			sum=cartMapper.selectCartProductCount(userId);
		}		
		return ServerResponse.createBySuccessMessage("查询购物车成功",sum);
	}
	
	
	
	
	
	
	
	
	/**
	 * 
	 * @Title: getCartVoLimit
	 * @Description: TODO 获得到某个用户的购物车信息
	 * @param @param userId
	 * @param @return    
	 * @return CartVo    
	 * @throws
	 */
	private CartVo getCartVoLimit(Integer userId){
		boolean allFlag=true;//用于判断表示是否全部选中
		//初始化购物车
		CartVo cartVo=new CartVo();
		//声明一个 总价格【一定要使用BigDecimal的构造器】
		cartVo.setCartTotalPrice(new BigDecimal("0"));
		//初始化购物车里面的商品vo
		List<CartProductVo> listVo=new ArrayList<CartProductVo>();
		//查询到所有商品
		List<Cart> list=cartMapper.selectByUid(userId);
		if(CollectionUtils.isNotEmpty(list)){
			for(Cart c:list){
				//得到商品的详细信息【也可放在数据库直接关联查询】
				Product p=new Product();
				p=productMapper.selectByPrimaryKey(c.getProductId());
				//封装vo对象
				CartProductVo productVo=new CartProductVo();
				/**
				 * 设置该条购物车的信息
				 */
				productVo.setCartId(c.getId());
				productVo.setUserId(c.getUserId());
				productVo.setProductId(c.getProductId());
				productVo.setChecked(c.getChecked());
				if(p!=null){
					/**
					 * 设置显示基本信息
					 */
					productVo.setProductName(p.getName());
					productVo.setProductMainImage(p.getMainImage());
					productVo.setProductSubtitle(p.getSubtitle());
					productVo.setProductStatus(p.getStatus());
					productVo.setProductPrice(p.getPrice());
					productVo.setProductStock(p.getStock());
					/**
					 * 设置可最大购买的数量
					 */
					int buyLimitCount=0;//可购买的最大数量
					if(p.getStock()>c.getQuantity()){//该商品的库存大于购物车里面购买的数量
						buyLimitCount=c.getQuantity();
						productVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
					}else{//库存不足,可购买的最大数量不能超过库存
						buyLimitCount=p.getStock();
						productVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
						//更新购物车里面购买的数量  将最大可购买的数量赋值给购物车
						Cart temp=new Cart();
						temp.setId(c.getId());
						temp.setQuantity(p.getStock());
						cartMapper.updateByPrimaryKeySelective(temp);
					}
					productVo.setQuantity(buyLimitCount);
					/**
					 * 设置该商品多个的价格【比如：牛仔裤00001号*3条 的价格】
					 * 单价*数量
					 */
					BigDecimal b1=BigDecimalUtil.mul(productVo.getProductPrice().doubleValue(),productVo.getQuantity().doubleValue());
					productVo.setProductTotalPrice(b1);
					/**
					 * productVo封装完毕
					 * 判断是否处于勾选的状态，如果是的话，那么就增加到购物车总价当中
					 */
					if(c.getChecked()==Const.Cart.CHECKED){
						BigDecimal cartTotalPrice=BigDecimalUtil.add(cartVo.getCartTotalPrice().doubleValue(),productVo.getProductTotalPrice().doubleValue());					
						cartVo.setCartTotalPrice(cartTotalPrice);
					}else{
						allFlag=false;//未全部勾选
					}
					listVo.add(productVo);
				}								
			}
		}
		cartVo.setCartProductVos(listVo);
		cartVo.setAllChecked(allFlag);
		cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		return cartVo;		
	}
	
	
	
	
}
