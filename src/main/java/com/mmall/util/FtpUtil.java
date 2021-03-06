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
	 * @Title: uploadFile
	 * @Description: TODO 上传文件
	 * @param @param remotePath
	 * @param @param list
	 * @param @return
	 * @param @throws IOException    
	 * @return boolean    
	 * @throws
	 */
	public boolean uploadFile(String remotePath,List<File> list) throws IOException{
		boolean uploaded=false;
		boolean result=this.connectServer(this.ip, this.port, this.user, this.password);
		if(result){
			FileInputStream fileInputStream=null;//输入流
			//连接ftp服务器
			try {
				ftpClient.makeDirectory(remotePath);//创建目录
				ftpClient.changeWorkingDirectory(remotePath);//改变上传的目录【默认是用户的家目录】
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
