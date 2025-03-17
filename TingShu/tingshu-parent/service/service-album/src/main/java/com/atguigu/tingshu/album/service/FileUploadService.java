package com.atguigu.tingshu.album.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @date: 2024/6/5 14:30
 * @author: yz
 * @version: 1.0
 */
public interface FileUploadService {
    /**
     * 文件上传
     * @param file
     * @return
     */
    String fileUpload(MultipartFile file);
}
