package com.mmall.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpUtil {
	private static final Logger logger=LoggerFactory.getLogger(FtpUtil.class);
	
	private static String ftpIp=PropertiesUtil_mmall.getProperty("ftp.server.ip");
	private static String ftpUser=PropertiesUtil_mmall.getProperty("ftp.user");
	private static String ftpPassword=PropertiesUtil_mmall.getProperty("ftp.password");
	
	private String ip;
	private int port;
	private String user;
	private String password;
	private FTPClient ftpClient;
	
	
	
	public FtpUtil(String ip, int port, String user, String password) {
		super();
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.password = password;
	}
	
	
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 
	 * @Title: uploda
	 * @Description: TODO 暴露的上传至ftp服务器的方法供调用
	 * @param @param list
	 * @param @return
	 * @param @throws IOException    
	 * @return boolean    
	 * @throws
	 */
	public static boolean uplod(String remotePath,List<File> list) throws IOException{
		FtpUtil ftpUtil=new FtpUtil(ftpIp, 21, ftpUser, ftpPassword);//初始化连接参数
		logger.info("开始连接ftp服务器");
		boolean result=ftpUtil.uploadFile(remotePath, list);//上传到ftp服务器默认目录下的/img目录下
		logger.info("上传文件至ftp服务器结束，上传结果:{}",result);
		return result;		
	}
	/**
	 * 
	 * @Title: uploadFile
	 * @Description: TODO 上传文件
	 * @param @param remotePath
	 * @param @param list
	 * @param @return
	 * @param @throws IOException    
	 * @return boolean    
	 * @throws
	 */
	private boolean uploadFile(String remotePath,List<File> list) throws IOException{
		boolean uploaded=false;
		FileInputStream fileInputStream=null;//输入流
		//连接ftp服务器
		if(connectServer(this.ip, this.port, this.user, this.password)){
			try {
				ftpClient.changeWorkingDirectory(remotePath);//需要上传的文件目录
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//设置成二进制文件类型
				ftpClient.enterLocalPassiveMode();//打开被动模式			
				for(File f:list){
					fileInputStream=new FileInputStream(f);
					ftpClient.storeFile(f.getName(),fileInputStream);
				}
				uploaded=true;				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				uploaded=false;
				logger.error("上传文件异常!",e);
			}finally {
				//释放连接
				fileInputStream.close();
				ftpClient.disconnect();
			}						
		}
		return uploaded;
	}
	
	/**
	 * 
	 * @Title: connectServer
	 * @Description: TODO 连接至服务器
	 * @param @param ip
	 * @param @param port
	 * @param @param user
	 * @param @param password
	 * @param @return    
	 * @return boolean    
	 * @throws
	 */
	private boolean connectServer(String ip,int port,String user,String password){
		boolean isSuccess=false;
		ftpClient=new FTPClient();
		try {
			ftpClient.connect(ip);
			isSuccess=ftpClient.login(user, password);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("连接ftp服务器异常！",e);
		}
		return isSuccess;
	}
	
}
