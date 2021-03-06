package com.mmall.vo;

import java.math.BigDecimal;
/**
 * 
 * @ClassName: OrderItemVo
 * @Description: TODO 订单中商品列表项
 * @author Barry
 * @date 2017年11月28日 上午11:03:17
 *
 */
public class OrderItemVo {
	private Long orderNo;

    private Integer productId;

    private String productName;

    private String productImage;
    
    private BigDecimal currentUnitPrice;//下单时该商品的单价

    private Integer quantity;//购买数量

    private BigDecimal totalPrice;//总价
    
    private String createTime;

	public Long getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Long orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public BigDecimal getCurrentUnitPrice() {
		return currentUnitPrice;
	}

	public void setCurrentUnitPrice(BigDecimal currentUnitPrice) {
		this.currentUnitPrice = currentUnitPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "OrderItemVo [orderNo=" + orderNo + ", productId=" + productId + ", productName=" + productName
				+ ", productImage=" + productImage + ", currentUnitPrice=" + currentUnitPrice + ", quantity=" + quantity
				+ ", totalPrice=" + totalPrice + "]";
	}
    
    
}
