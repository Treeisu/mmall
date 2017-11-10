package com.mmall.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @ClassName: PropertiesUtil
 * @Description: TODO 读取配置文件  工具类
 * @author Barry
 * @date 2017年11月10日 下午2:25:11
 *
 */
public class PropertiesUtil {
	private static Logger logger=LoggerFactory.getLogger(PropertiesUtil.class);
	private static Properties props;
	static{
		String fileName="mmall.properties";
		props=new Properties();
		try {
			props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("配置文件读取异常！",e);
			e.printStackTrace();
		}
	}
	
	public static String getProperty(String key){
		String value=props.getProperty(key.trim());
		if(StringUtils.isBlank(value)){
			return null;
		}
		return value.trim();		
	}
	public static String getProperty(String key,String defaultValue){
		String value=props.getProperty(key.trim());
		if(StringUtils.isBlank(value)){
			value=defaultValue;
		}
		return value.trim();		
	}
	
	
}
