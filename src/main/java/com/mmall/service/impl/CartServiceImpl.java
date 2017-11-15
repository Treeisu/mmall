package com.mmall.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
	 * 商品加入购物车
	 */
	@Override
	public ServerResponse<CartVo> add(Integer userId, Integer count, Integer productId) {
		// TODO Auto-generated method stub
		//查询用户是否已添加过此商品进购物车
		//校验参数
		Product product=new Product();
		if(productId==null||count==null){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误！");
		}else{
			product=productMapper.selectByPrimaryKey(productId);
			if(product==null){
				return ServerResponse.createByErrorMessage("添加失败，该商品不存在！");
			}else{				
				Cart cart=cartMapper.selectByUidAndPid(userId, productId);
				if(cart!=null){//购物车中已存在过此商品					
					cart.setQuantity(count);					
					cartMapper.updateByPrimaryKeySelective(cart);//更新
				}else{//未添加过，则新建一个										
					Cart c=new Cart();
					c.setQuantity(count);
					c.setProductId(productId);
					c.setUserId(userId);
					c.setChecked(Const.Cart.CHECKED);					
					cartMapper.insert(c);//保存
				}
				CartVo cartVo=this.getCartVoLimit(userId);		
				return ServerResponse.createBySuccessMessage("查询购物车成功", cartVo);
			}
		}
		
	}
	@Override
	public ServerResponse<CartVo> update(Integer userId, Integer count, Integer productId){
		Product product=new Product();
		if(productId==null||count==null){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误！");
		}else{
			product=productMapper.selectByPrimaryKey(productId);
			if(product==null){
				return ServerResponse.createByErrorMessage("更新失败，该商品不存在！");
			}else{
				Cart cart=cartMapper.selectByUidAndPid(userId, productId);
				if(cart!=null){
					cart.setQuantity(count);
					cartMapper.updateByPrimaryKeySelective(cart);//更新购物车
				}				
			}
		}
		return null;		
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
						BigDecimal cartTotalPrice=BigDecimalUtil.add(cartVo.getCartTotalPrice().doubleValue(),productVo.getProductPrice().doubleValue());					
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
