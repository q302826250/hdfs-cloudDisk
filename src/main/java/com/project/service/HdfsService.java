package com.project.service;

import org.apache.hadoop.fs.BlockLocation;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * @Auther: zhangjuntao
 * @Date: 2021/6/4 - 06 - 04 - 14:42
 * @Description :com.project.service
 * @Version: 1.0
 */
public interface HdfsService {
     public List<Map<String,String>> listStatus (String path) throws InterruptedException, IOException, URISyntaxException;

     //判断HDFS文件是否存在
     boolean existFile(String path) throws InterruptedException, IOException, URISyntaxException;

     //创建文件夹
     boolean mkdir(String path) throws InterruptedException, IOException, URISyntaxException;

     //获取某个文件的信息
     Map<String,String> geFileInfo(String path) throws InterruptedException, IOException, URISyntaxException;

    //根据类型全盘查找文件，也可以多传递一个参数，约定在一个目录下查找指定类型的文件
    List<Map<String,String>> listStatus(int type) throws InterruptedException, IOException, URISyntaxException;

    //HDFS重命名文件
     boolean renameFile(String oldName, String newName) throws InterruptedException, IOException, URISyntaxException;

     //删除文件
     boolean deleteFile(String path) throws InterruptedException, IOException, URISyntaxException;

    //获取某个文件的信息
    Map<String,String> getFileInfo(String path) throws InterruptedException, IOException, URISyntaxException;

    //HDFS文件复制
    void copyFile(String soursePath, String targetPath) throws Exception;

    //下载HDFS文件
    ResponseEntity<InputStreamResource> downloadFile(String path, String fileName) throws InterruptedException, IOException, URISyntaxException;

    //打开HDFS上的文件并返回byte数组
    byte[] openFileToBytes(String pathh) throws InterruptedException, IOException, URISyntaxException;

    //上传HDFS文件
    void uploadFile(String path, String uploadPath) throws InterruptedException, IOException, URISyntaxException;

    //读取HDFS文件内容
    String readFile(String path) throws InterruptedException, IOException, URISyntaxException;

    //创建文件
    void createFile(String path, MultipartFile file) throws Exception;

    //获取某个文件在HDFS的集群位置
    BlockLocation[] getFileBlockLocations(String path, MultipartFile file) throws Exception;

    //以打包的形式下载目录
    ResponseEntity<byte[]> downloadDirectory(String path, String fileName) throws IOException;

    //读取配置信息
    Map<String, String> getConfigurationInfoAsMap() throws InterruptedException, IOException, URISyntaxException;
}
