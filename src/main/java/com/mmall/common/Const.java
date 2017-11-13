package com.mmall.common;

import java.util.Set;

import com.google.common.collect.Sets;

public class Const {
	public  static final String CURRENT_USER="currentUser";
	public  static final String EMAIL="email";
	public  static final String USERNAME="username";
	public interface Role{//内部类
		int ROLE_CUSTOMER=0;
		int ROLE_ADMIN=1;
	}
	public interface ProductListOrderBy{//内部类
		Set<String> PRICE_ASC_DESC=Sets.newHashSet("price_desc","price_asc");
	}
	public enum ProductStatus{		
		ON_SALE(1,"在售状态"),
		NO_SALE(0,"下架状态");
		private int code;
		private String value;
		private ProductStatus( int code,String value) {
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
