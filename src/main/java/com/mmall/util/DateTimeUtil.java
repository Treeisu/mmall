package com.mmall.util;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 
 * @ClassName: DateTimeUtil
 * @Description: TODO 数据库中取出的数据是毫秒数 此工具类是对毫秒数进行转化
 * @author Barry
 * @date 2017年11月10日 下午3:49:47
 *
 */
public class DateTimeUtil {
	private static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";
	public static Date strToDate(String dateTimeStr,String formatStr){
		DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern(formatStr);
		DateTime dateTime=dateTimeFormatter.parseDateTime(dateTimeStr);				
		return dateTime.toDate();		
	};
	public static String dateToStr(Date date,String formatStr){
		if(date==null){
			return StringUtils.EMPTY;
		}
		DateTime dateTime=new DateTime(date);
		
		return dateTime.toString(formatStr);
	};
	public static Date strToDate(String dateTimeStr){
		DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern(DateTimeUtil.STANDARD_FORMAT);
		DateTime dateTime=dateTimeFormatter.parseDateTime(dateTimeStr);				
		return dateTime.toDate();		
	};
	public static String dateToStr(Date date){
		if(date==null){
			return StringUtils.EMPTY;
		}
		DateTime dateTime=new DateTime(date);
		
		return dateTime.toString(DateTimeUtil.STANDARD_FORMAT);
	};
}
