package com.mmall.common;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
/**
 * 
 * @ClassName: ServerResponse 
 * @Description: TODO 用于返回给前端的封装类对象
 * @author Barry
 * @date 2017年11月6日 下午4:11:09
 *
 * @param <T>
 */
@SuppressWarnings("serial")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)//这个注解表示：在对json进行序列化的时候，当data或者msg没有进行初始化是null，则不返回给前端
public class ServerResponse<T> implements Serializable{
	private int status;
	private String msg;
	private T data;
	
	
	private ServerResponse() {
		super();
	}
	private ServerResponse(int status) {
		super();
		this.status = status;
	}	
	private ServerResponse(int status, String msg) {
		super();
		this.status = status;
		this.msg = msg;
	}	
	private ServerResponse(int status, T date) {
		super();
		this.status = status;
		this.data = date;
	}
	private ServerResponse(int status, String msg, T date) {
		super();
		this.status = status;
		this.msg = msg;
		this.data = date;
	}
	
	public int getStatus() {
		return status;
	}
	public String getMsg() {
		return msg;
	}
	public T getDate() {
		return data;
	}
	/**
	 * 
	 * @Title: isSuccess
	 * @Description: TODO 如果请求的状态和枚举的成功的状态码一样，返回true那么即为请求成功
	 * @param @return    
	 * @return boolean    
	 * @throws
	 */
	//判断请求是否成功
	@JsonIgnore //此注解表示，此方法在对status初始化时，使之不在序列化之中，不会返回给前端
	public boolean isSuccess(){
		return this.status==ResponseCode.SUCCESS.getCode();
	}
	//判断请求是否成功
	@JsonIgnore //此注解表示，此方法在对status初始化时，使之不在序列化之中，不会返回给前端
	public boolean isError(){
		return this.status==ResponseCode.ERROR.getCode();
	}
	public static <T> ServerResponse<T> createBySuccess(){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());		
	}
	//请求成功时，可以定义需要返回的msg
	public static <T> ServerResponse<T> createBySuccessMessage(String msg){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);		
	}
	//请求成功时，可以填充需要返回的数据
	public static <T> ServerResponse<T> createBySuccessMessage(T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);		
	}
	//请求成功时，可以填充需要返回msg提示和数据data
	public static <T> ServerResponse<T> createBySuccessMessage(String msg,T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);		
	}
	

	//请求失败，初始化错误状态码
	public static <T> ServerResponse<T> createByError(){
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDescription());		
	}
	//请求失败时，可以定义需要返回的msg
	public static <T> ServerResponse<T> createByErrorMessage(String msg){
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(),msg);		
	}
	//请求失败时，可以定义需要返回的msg
	public static <T> ServerResponse<T> createByErrorMessage(int errorCode,String msg){
		return new ServerResponse<T>(errorCode,msg);		
	}
	
	
	
	
	
}
