package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * @ClassName: OrderProductVo
 * @Description: TODO 用于显示订单中的商品
 * @author Barry
 * @date 2017年11月28日 上午11:03:49
 *
 */
public class OrderProductVo {
	private List<OrderItemVo> orderItemVoList;
	private BigDecimal productTotalPrice;
	private String imgHost;
	public List<OrderItemVo> getOrderItemVoList() {
		return orderItemVoList;
	}
	public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
		this.orderItemVoList = orderItemVoList;
	}
	public BigDecimal getProductTotalPrice() {
		return productTotalPrice;
	}
	public void setProductTotalPrice(BigDecimal productTotalPrice) {
		this.productTotalPrice = productTotalPrice;
	}
	public String getImgHost() {
		return imgHost;
	}
	public void setImgHost(String imgHost) {
		this.imgHost = imgHost;
	}
	
}
