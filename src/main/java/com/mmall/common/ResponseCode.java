package com.mmall.common;
/**
 *
 * 请求响应的状态枚举类
 * @author Barry
 * @date 2017年11月6日 下午3:58:09
 *
 */
public enum ResponseCode {	
	SUCCESS(0,"SUCCESS"),
	ERROR(1,"ERROR"),
	NEED_LOGIN(10,"NEED_LOGIN"),
	ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");
	private final int code;
	private final String description;	
	ResponseCode(int code, String description) {
		this.code = code;
		this.description = description;
	}
	public int getCode() {
		return code;
	}
	public String getDescription() {
		return description;
	}
	
}
