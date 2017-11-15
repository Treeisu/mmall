package com.mmall.vo;

import java.math.BigDecimal;

/**
 * 
 * @ClassName: CartProductVo
 * @Description: TODO 购物车中显示商品的vo
 * @author Barry
 * @date 2017年11月15日 上午10:25:59
 *
 */
public class CartProductVo {
	private Integer cartId;//购物车id

    private Integer userId;//用户id

    private Integer productId;//产品id

    private Integer quantity;//已购买数量

    private Integer checked;//是否选中
    
    private String productName;
    
    private String productSubtitle;//商品副标题
    
    private String productMainImage;//商品主图
    
    private BigDecimal productPrice;//商品在售单价
    
    private BigDecimal productTotalPrice;//商品在售总价【多个商品】
    
    private Integer productStatus;//商品是否在售
    
    private Integer productStock;//商品库存
    
    private String limitQuantity;//限制数量的返回结果【是否超过了库存】

	public Integer getCartId() {
		return cartId;
	}

	public void setCartId(Integer id) {
		this.cartId = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getChecked() {
		return checked;
	}

	public void setChecked(Integer checked) {
		this.checked = checked;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getProductSubtitle() {
		return productSubtitle;
	}

	public void setProductSubtitle(String productSubtitle) {
		this.productSubtitle = productSubtitle;
	}

	public String getProductMainImage() {
		return productMainImage;
	}

	public void setProductMainImage(String productMainImage) {
		this.productMainImage = productMainImage;
	}

	public BigDecimal getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}

	public BigDecimal getProductTotalPrice() {
		return productTotalPrice;
	}

	public void setProductTotalPrice(BigDecimal productTotalPrice) {
		this.productTotalPrice = productTotalPrice;
	}

	public Integer getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(Integer productStatus) {
		this.productStatus = productStatus;
	}

	public Integer getProductStock() {
		return productStock;
	}

	public void setProductStock(Integer productStock) {
		this.productStock = productStock;
	}

	public String getLimitQuantity() {
		return limitQuantity;
	}

	public void setLimitQuantity(String limitQuantity) {
		this.limitQuantity = limitQuantity;
	}	    
    
}
