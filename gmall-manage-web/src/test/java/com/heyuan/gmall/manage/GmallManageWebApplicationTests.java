package com.heyuan.gmall.manage;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageWebApplicationTests {

    @Test
    public void contextLoads() throws IOException, MyException {

        //配置fdfs的全局连接地址
        String tracker = GmallManageWebApplicationTests.class.getResource("/tracker.conf").getPath();//获取配置文件路径

        ClientGlobal.init(tracker);

        TrackerClient trackerClient = new TrackerClient();
        //获得一个trackerserver的实例
        TrackerServer trackerServer = trackerClient.getConnection();
        //通过tracker获得storage客户端
        StorageClient storageClient = new StorageClient(trackerServer, null);

        String[] uploadInfos = storageClient.upload_file("g:/view.jpg", "jpg", null);

        String url="http://10.127.106.56";

        for (String uploadInfo : uploadInfos){
            url+="/"+uploadInfo;
        }
        System.out.println(url);

    }

}
