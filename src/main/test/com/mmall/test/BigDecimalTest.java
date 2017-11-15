package com.mmall.test;

import java.math.BigDecimal;

import org.junit.Test;

public class BigDecimalTest {
	/**
	 * 
	 * @Title: test1
	 * @Description: TODO 浮点型运算
	 * @param     
	 * @return void    
	 * @throws
	 */
	@Test
	public void test1(){
		System.out.println("0.05+0.01="+(0.05+0.01));
		System.out.println("1.00-0.42="+(1.00-0.42));
		System.out.println("4.015*100="+(4.015*100));
		System.out.println("123.3/100="+(123.3/100));
	}
	/**
	 * 
	 * @Title: test2
	 * @Description: TODO 不使用BigDecimal的String构造器
	 * @param     
	 * @return void    
	 * @throws
	 */
	@Test
	public void test2(){
		BigDecimal b1=new BigDecimal(0.05);
		BigDecimal b2=new BigDecimal(0.01);
		System.out.println("0.05+0.01="+(b1.add(b2)));
	}
	/**
	 * 
	 * @Title: test3
	 * @Description: TODO 使用BigDecimal的String构造器
	 * @param     
	 * @return void    
	 * @throws
	 */
	@Test
	public void test3(){
		BigDecimal b1=new BigDecimal("0.05");
		BigDecimal b2=new BigDecimal("0.01");
		System.out.println("0.05+0.01="+(b1.add(b2)));
	}
}
