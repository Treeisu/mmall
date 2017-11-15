package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * @ClassName: CartVo
 * @Description: TODO 购物车显示类【里面包含多个购物车商品显示vo】
 * @author Barry
 * @date 2017年11月15日 上午10:46:50
 *
 */
public class CartVo {
	private List<CartProductVo> cartProductVos;//需要显示的商品
	
	private BigDecimal cartTotalPrice;//整个购物车的总价
	
	private Boolean allChecked;//整个购物车是否全部勾选
	
	private String imageHost;//图片的服务器地址

	public List<CartProductVo> getCartProductVos() {
		return cartProductVos;
	}

	public void setCartProductVos(List<CartProductVo> cartProductVos) {
		this.cartProductVos = cartProductVos;
	}

	public BigDecimal getCartTotalPrice() {
		return cartTotalPrice;
	}

	public void setCartTotalPrice(BigDecimal cartTotalPrice) {
		this.cartTotalPrice = cartTotalPrice;
	}

	public Boolean getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(Boolean allChecked) {
		this.allChecked = allChecked;
	}

	public String getImageHost() {
		return imageHost;
	}

	public void setImageHost(String imageHost) {
		this.imageHost = imageHost;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allChecked == null) ? 0 : allChecked.hashCode());
		result = prime * result + ((cartProductVos == null) ? 0 : cartProductVos.hashCode());
		result = prime * result + ((cartTotalPrice == null) ? 0 : cartTotalPrice.hashCode());
		result = prime * result + ((imageHost == null) ? 0 : imageHost.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CartVo other = (CartVo) obj;
		if (allChecked == null) {
			if (other.allChecked != null)
				return false;
		} else if (!allChecked.equals(other.allChecked))
			return false;
		if (cartProductVos == null) {
			if (other.cartProductVos != null)
				return false;
		} else if (!cartProductVos.equals(other.cartProductVos))
			return false;
		if (cartTotalPrice == null) {
			if (other.cartTotalPrice != null)
				return false;
		} else if (!cartTotalPrice.equals(other.cartTotalPrice))
			return false;
		if (imageHost == null) {
			if (other.imageHost != null)
				return false;
		} else if (!imageHost.equals(other.imageHost))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CartVo [cartProductVos=" + cartProductVos + ", cartTotalPrice=" + cartTotalPrice + ", allChecked="
				+ allChecked + ", imageHost=" + imageHost + "]";
	}
	
	
}
