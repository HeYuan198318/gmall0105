package com.heyuan.gmall.user.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class UmsUploadUtil {
    public static String uploadImage(MultipartFile multipartFile) throws IOException, MyException {
        String imgUrl = "http://10.127.105.56";
        //上传图片到服务器
        //配置fdfs的全局连接地址
        String tracker = UmsUploadUtil.class.getResource("/tracker.conf").getPath();//获取配置文件路径

        ClientGlobal.init(tracker);

        TrackerClient trackerClient = new TrackerClient();
        //获得一个trackerserver的实例
        TrackerServer trackerServer = trackerClient.getConnection();
        //通过tracker获得storage客户端
        StorageClient storageClient = new StorageClient(trackerServer, null);

        //获取上传二进制对象
        byte[] bytes = multipartFile.getBytes();
        /**
         * 获取文件后缀名称
         */
        //文件的名称
        String originalFilename = multipartFile.getOriginalFilename();//9.gif
        System.out.println(originalFilename);
        int i = originalFilename.lastIndexOf(".");
        String extName = originalFilename.substring(i + 1);

        String[] uploadInfos = storageClient.upload_file(bytes, extName, null);


        for (String uploadInfo : uploadInfos) {
            imgUrl += "/" + uploadInfo;

        }

        return imgUrl;

    }
}
