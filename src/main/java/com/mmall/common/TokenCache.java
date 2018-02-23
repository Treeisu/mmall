package com.mmall.common;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 
 * @ClassName: TokenCache
 * @Description: TODO
 * @author Barry
 * @date 2017年11月7日 下午2:15:05
 *
 */
public class TokenCache {
	private static Logger logger=LoggerFactory.getLogger(TokenCache.class);
	public static final String TOKEN_PREFIX="token_";
	
	
	/**
	 * localCache方法
	 */
	public static LoadingCache<String,String> localCache=CacheBuilder.newBuilder()
			.initialCapacity(1000)//设置缓存的初始化容量
			.maximumSize(10000)//设置缓存的最大容量，当超过这个时候guawa会使用LRU算法[最小使用算法]去移除缓存项
			.expireAfterAccess(12, TimeUnit.HOURS)//设置缓存的有效期 为12个小时
			.build(new CacheLoader<String,String>(){
				//默认的数据加载实现；当调用get方法的时候key没有命中，那么调用此方法进行加载
				@Override
				public String load(String s) throws Exception{
					return "null";
				}
			});	
	/**
	 * setKey的方法
	 */
	public static void setKey(String key,String value){
		localCache.put(key, value);
	}
	/**
	 * getkey的方法
	 */
	public static String getKey(String key){
		String value=null;
		try {
			value=localCache.get(key);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("localCache get ERROR",e);
		}
		if("null".equals(value))
			return null;
		else
			return value;
	}
}
