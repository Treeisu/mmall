package com.mmall.common;


import java.util.Set;

import com.google.common.collect.Sets;

public class Const {
	public  static final String CURRENT_USER="currentUser";
	public  static final String EMAIL="email";//关键字类型
	public  static final String USERNAME="username";//关键字类型
	public interface Role{//内部类
		int ROLE_CUSTOMER=0;
		int ROLE_ADMIN=1;
	}
	public interface Cart{//内部类
		int CHECKED=1;//选中状态
		int UN_CHECKED=0;//未选中状态
		//某种商品的库存是否比购物车中购买的数量多
		String LIMIT_NUM_FAIL="LIMIT_NUM_FAIL";
		String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
	}
	public interface ProductListOrderBy{//内部类
		Set<String> PRICE_ASC_DESC=Sets.newHashSet("price_desc","price_asc");
	}
	/**
	 *
	 * @Description: TODO 销售状态 【内部类】
	 * @author Barry
	 * @date 2017年11月16日 下午1:47:30
	 *
	 */
	public enum ProductStatusEnum{		
		ON_SALE(1,"在售状态"),
		NO_SALE(0,"下架状态");
		private final int code;
		private final String value;
		ProductStatusEnum(int code, String value) {
			this.value = value;
			this.code = code;
		}
		public int getCode() {
			return code;
		}
		public String getValue() {
			return value;
		}	
		public static ProductStatusEnum codeof(int code){
			for(ProductStatusEnum p:values()){
				if(p.getCode()==code){
					return p;
				}
			}
			throw new RuntimeException();
		}
	}
	public enum PaymentTypeEnum{		
		ONLINE_PAY(1,"在线支付");
		private final int code;
		private final String value;
		PaymentTypeEnum(int code, String value) {
			this.value = value;
			this.code = code;
		}
		public int getCode() {
			return code;
		}
		public String getValue() {
			return value;
		}	
		public static PaymentTypeEnum codeof(int code){
			for(PaymentTypeEnum p:values()){
				if(p.getCode()==code){
					return p;
				}
			}
			throw new RuntimeException();
		}
	}
	/**
	 * 
	 * @ClassName: OrderStatus
	 * @Description: TODO 订单状态类枚举
	 * @author Barry
	 * @date 2017年11月24日 下午3:06:00
	 *
	 */
	public enum OrderStatusEnum{		
		CANCELED(0,"已取消"),
		NOTSHOW(1,"不显示"),
		NO_PAY(10,"未支付"),
		PAIED(20,"已支付"),
		SHIPPED(40,"已发货"),
		ORDER_SUCCESS(50,"订单完成"),
		ORDER_CLOSE(60,"订单关闭");
		private final int code;
		private final String value;
		OrderStatusEnum(int code, String value) {
			this.value = value;
			this.code = code;
		}
		public int getCode() {
			return code;
		}
		public String getValue() {
			return value;
		}		
		public static OrderStatusEnum codeof(int code){
			for(OrderStatusEnum p:values()){
				if(p.getCode()==code){
					return p;
				}
			}
			throw new RuntimeException();
		}
	}
	/**
	 * 
	 * @ClassName: AlipayCallback
	 * @Description: TODO 支付宝调用的状态
	 * @author Barry
	 * @date 2017年11月24日 下午3:16:13
	 *
	 */
	public interface AlipayCallback{
		String TRADE_STATUS_WAIT_BUYER_PAY="WAIT_BUYER_PAY";
		String TRADE_STATUS_TRADE_SUCCESS="TRADE_SUCCESS";
		String RESPONSE_SUCCESS="success";
		String RESPONSE_FAILED="failed";
	}
	/**
	 * 
	 * @ClassName: PayPlatFormEnum
	 * @Description: TODO 支付平台
	 * @author Barry
	 * @date 2017年11月24日 下午3:27:00
	 *
	 */
	public enum PayPlatformEnum{		
		ALIPAY(1,"支付宝"),
		WECHATE(2,"微信支付");
		private final int code;
		private final String value;
		PayPlatformEnum(int code, String value) {
			this.value = value;
			this.code = code;
		}
		public int getCode() {
			return code;
		}
		public String getValue() {
			return value;
		}				
	}
}
