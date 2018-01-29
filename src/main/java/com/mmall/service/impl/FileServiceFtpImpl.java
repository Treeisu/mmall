package com.mmall.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.mmall.service.IFileServiceFTP;
import com.mmall.util.FtpUtil;


@Service("iFileServiceFtp")
public class FileServiceFtpImpl implements IFileServiceFTP{
	private Logger logger=LoggerFactory.getLogger(FileServiceFtpImpl.class);
	/**
	 * 上传成功则返回文件名
	 */
	public String upload(String path,String remotePath,MultipartFile file,FtpUtil ftpUtil){
		/**
		 * 一、先上传到本地
		 * 二、再上传到FTP服务器
		 * 三、删除本地的文件
		 */
		//获得文件的扩展名
		String fileExtensionName=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
		//重新命名文件【得到不重复的文件名】
		String uploadFileName=UUID.randomUUID().toString()+"."+fileExtensionName;		
		//创建文件夹目录【项目路径下的】
		File fileDir=new File(path);
		if(!fileDir.exists()){//如果该文件夹不存在,进行创建
			fileDir.setWritable(true);//首先，赋予可写权限
			fileDir.mkdirs();
		}
		//封装成本地tomcat保存的  文件实体
		File fileObj=new File(path,uploadFileName);
		try {
			logger.info("开始上传文件至本地,上传的路径:{},上传的文件名:{}",path,uploadFileName);
			//一、开始上传到本地
			file.transferTo(fileObj);
			//二、将fileObj上传至ftp服务器上；
			ftpUtil.uploadFile(remotePath,Lists.newArrayList(fileObj));
			logger.info("开始删除本地的上传文件");
			//三、将项目路径下的upload文件删除
			fileObj.delete();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("上传文件出错！",e);
			return null;
		}
		return fileObj.getName();	 	
	}
	
	
	
	
}
