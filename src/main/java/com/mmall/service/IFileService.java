package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @ClassName: IFileService
 * @Description: TODO 处理文件上传
 * @author Barry
 * @date 2017年11月13日 上午10:58:18
 *
 */
public interface IFileService {
	String upload(MultipartFile file,String path);
}
