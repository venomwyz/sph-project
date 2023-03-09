package com.atguigu.gmall.product.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

//完成初始化任务
    static {
        try {

            ClientGlobal.init("file.conf");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 文件上传
     * @param file
     * @return
     */

    public static String upload1(MultipartFile file){

        try {
            //连接到tracker

            TrackerClient trackerClient = new TrackerClient();

            //通过tracker获取storage
            TrackerServer connection = trackerClient.getConnection();

            //通过storage获取上传文件
            StorageClient storageClient = new StorageClient();

            //获取文件的组名和全量名包括扩展名
            String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            //文件上传
            //第一个参数：字节码文件
            //第二个参数：文件扩展名
            //第三个参数：0：组名  1，文件的完全路径名+文件名

            String[] strings = storageClient.upload_file(file.getBytes(), filenameExtension, null);
            //返回组名和全量名
            return strings[0]+"/"+strings[1];
        } catch (Exception e) {

            return null;
        }

    }

    /**
     * 文件下载
     * @param
     * @return
     */


    public static byte[] downLoad(String groupName,String fileName){

        try {
            //连接到tracker
            TrackerClient trackerClient = new TrackerClient();
            //通过tracker获取storage
            TrackerServer connection = trackerClient.getConnection();

            //通过storage获取文件
            StorageClient storageClient = new StorageClient();


            return storageClient.download_file(groupName,fileName);

        } catch (Exception e) {

            return null;
        }

    }

    /**
     * 文件删除
     * @param groupName
     * @param fileName
     * @return
     */

    public static boolean deleteFile(String groupName,String fileName){

        try {
            //连接到tracker

            TrackerClient trackerClient = new TrackerClient();

            //通过tracker获取storage
            TrackerServer connection = trackerClient.getConnection();

            //通过storage获取文件
            StorageClient storageClient = new StorageClient();


            int i = storageClient.delete_file(groupName, fileName);
            return i==0;

        } catch (Exception e) {

        }
        throw new RuntimeException();

    }

    /**
     * 文件下载
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        // 创建storage客户端对象
        StorageClient storageClient = new StorageClient(trackerServer, null);
        // 下载文件，第一个参数为groupName，第二个参数为文件路径
        byte[] bytes = storageClient.download_file("group1", "M00/00/03/wKjIgGP1-bqAJRbaAAFNpxDhNBI207.jpg");
        // 指定保存的路径
        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\1.jpg"));
        // 写入到文件中
        fileOutputStream.write(bytes);
        // 关闭流
        fileOutputStream.close();
    }



}
