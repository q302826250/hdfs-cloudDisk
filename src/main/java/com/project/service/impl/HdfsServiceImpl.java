package com.project.service.impl;

import com.project.service.HdfsService;
import com.project.util.YcFileUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Auther: zhangjuntao
 * @Date: 2021/6/4 - 06 - 04 - 14:42
 * @Description :com.project.service
 * @Version: 1.0
 */
@Service
public class HdfsServiceImpl implements HdfsService {
    @Value("${hdfs.path}")
    private String path;
    @Value("${hdfs.username}")
    private String username;

    private final int bufferSize = 1024*1024*64;//缓存

    //获取HDFS配置信息
    public Configuration getConfiguration(){
       Configuration configuration = new Configuration();
       configuration.set("fs.defaultFS",path);
       //其他参数
       return configuration;
    }

    @Override
    public List<Map<String, String>> listStatus(String path) throws InterruptedException, IOException, URISyntaxException {
       if(StringUtils.isEmpty(path)){
           return null;
       }
       if(!existFile(path)){
           return null;
       }
        FileSystem fs = getFileSystem();
       //目标路径
        Path srcPath = new Path(path);
        FileStatus[]fileStatuses = fs.listStatus(srcPath); //调用listStatus方法

        if(fileStatuses==null || fileStatuses.length<=0){
            return null;
        }
        List<Map<String,String>> returnList = new ArrayList<>();
        for (FileStatus file:fileStatuses){
           Map<String,String> map = fileStatusToMap(file);
           returnList.add(map);
        }
        fs.close();
        return returnList;
    }

    //获取HDFS文件系统对象
    private FileSystem getFileSystem() throws URISyntaxException, IOException, InterruptedException {
        //客户端去操作hdfs时是有一个用户身份的，默认情况下hdfs客户端api会从
        //jvm中获取一个参数作为自己的用户身份
        //也可以在构造客户端fs对象时，通过参数传递进去
        //路径，配置项，用户名
        FileSystem fileSystem = FileSystem.get(new URI(path),getConfiguration(),username);
        return fileSystem;
    }

    //判断HDFS文件是否存在
    @Override
    public boolean existFile(String path) throws InterruptedException, IOException, URISyntaxException {
        if(StringUtils.isEmpty(path)){
            return false;
        }
        FileSystem fs = getFileSystem();
        Path srcPath = new Path(path);
        boolean isExists = fs.exists(srcPath);
        return isExists;
    }

    //创建文件夹
    @Override
    public boolean mkdir(String path) throws InterruptedException, IOException, URISyntaxException {
        if(StringUtils.isEmpty(path)){
            return false;
        }
        if(existFile(path)){
            return true;
        }
        FileSystem fs = getFileSystem();
        //目标路径
        Path srcPath = new Path(path);
        boolean isOk = fs.mkdirs(srcPath);
        fs.close();
        return isOk;
    }
    //获取某个文件的信息
    @Override
    public Map<String,String> geFileInfo(String path) throws InterruptedException, IOException, URISyntaxException {
        if(StringUtils.isEmpty(path)){
            return null;
        }
        if(!existFile(path)){
            return null;
        }
        FileSystem fs = getFileSystem();
        //目标路径
        Path srcPath = new Path(path);
        FileStatus fileStatus = fs.getFileStatus(srcPath);
        return fileStatusToMap(fileStatus);
    }
    //根据类型全盘查找文件，也可以多传递一个参数，约定在一个目录下查找指定类型的文件
    @Override
    public List<Map<String,String>> listStatus(int type) throws InterruptedException, IOException, URISyntaxException {
        //查找全盘所有文件
        String path = "/";
        //目标路径
        Path srcPath = new Path(path);
        List<Map<String, String>> returnList = new ArrayList<>();
        String reg = null;
        if (type == 1) {
            reg = ".+(.jpeg|.jpg|.png|.bmp|.gif)$";
        } else if (type == 2) {
            reg = ".+(.txt|.rtf|.doc|.xls|.xlsx|.html|.htm|.xml)$";
        } else if (type == 3) {
            reg = ".+(.mp4|.avi|.wmv)$";
        } else if (type == 4) {
            reg = ".+(.mp3|.wav)$";
        } else if (type == 5) {
            reg = "^\\S+\\.*$";
        }
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        search(srcPath, returnList, pattern);
        return returnList;
    }
//递归查找
    private void search(Path srcPath, List<Map<String, String>> returnList, Pattern pattern) throws InterruptedException, IOException, URISyntaxException {
        FileSystem fs = getFileSystem();
        FileStatus[] fileStatuses = fs.listStatus(srcPath);
        if (fileStatuses != null && fileStatuses.length > 0) {
            for (FileStatus file : fileStatuses) {
                boolean result = file.isFile();
                if (!result) {
                    //是目录 则递归
                    search(file.getPath(), returnList, pattern);
                } else {
                    //是文件 ,则判断类型
                    boolean b = pattern.matcher(file.getPath().getName()).find();
                    if (b) {
                        Map<String, String> map = this.fileStatusToMap(file);
                        returnList.add(map);
                    }
                }
            }
        }
    }
//封装Map->fileStatus
    private Map<String, String> fileStatusToMap(FileStatus file) {
        Map<String,String> map = new HashMap<>();
        Path p = file.getPath();
        map.put("fileName",p.getName());
        String filePath = p.toUri().toString();
        map.put("filePath",filePath);                   //hdfs://node1:9000/idea/a.txt
            String relativePath = filePath.substring(this.path.length());   // /idea/a.txt

        map.put("relativePath",relativePath); //相对路径 ->当前路径

        map.put("parentPath",p.getParent().toUri().toString().substring(this.path.length()));      // /idea

        map.put("owner",file.getOwner());
        map.put("group",file.getGroup());
        map.put("isFile",file.isFile()+"");
        map.put("duplicates",file.getReplication()+""); //副本
        map.put("size", YcFileUtil.formatFileSize(file.getLen()));
        map.put("rights",file.getPermission().toString());
        map.put("modifyTime",YcFileUtil.formatTime(file.getModificationTime()));
        return map;
    }
    //HDFS重命名文件
    @Override
    public boolean renameFile(String oldName, String newName) throws InterruptedException, IOException, URISyntaxException {
        if(StringUtils.isEmpty(oldName) || StringUtils.isEmpty(newName)){
            return false;
        }
        FileSystem fs = getFileSystem();
        //源文件目标路径
        Path oldPath = new Path(oldName);
        //重命名目标路径
        Path newPath = new Path(newName);
        boolean isOk = fs.rename(oldPath,newPath);
        fs.close();
        return isOk;
    }
    //删除文件
    @Override
    public boolean deleteFile(String path) throws InterruptedException, IOException, URISyntaxException {
        if(StringUtils.isEmpty(path)){
            return false;
        }
        if(!existFile(path)){
            return false;
        }
        FileSystem fs = getFileSystem();
        Path srcPath = new Path(path);
        boolean isOk = fs.delete(srcPath,true);//后面的boolean值是否递归删除目录
        fs.close();
        return isOk;
    }
   //获取某个文件的信息
    @Override
    public Map<String,String> getFileInfo(String path) throws InterruptedException, IOException, URISyntaxException {
        if(StringUtils.isEmpty(path)){
            return null;
        }
        if(!existFile(path)){
            return null;
        }
        FileSystem fs = getFileSystem();
        //目标路径
        Path srcPath = new Path(path);
        FileStatus fileStatus = fs.getFileStatus(srcPath);
        return  fileStatusToMap(fileStatus);
    }
    //HDFS文件复制
    @Override
    public void copyFile(String soursePath, String targetPath) throws Exception {
        if (StringUtils.isEmpty(soursePath) || StringUtils.isEmpty(targetPath)) {
            return;
        }
        FileSystem fs = getFileSystem();
        //原文件路径
        Path oldPath = new Path(soursePath);
        //目标路径
        Path newPath = new Path(targetPath);
        FSDataInputStream inputStream = null;
        FSDataOutputStream outputStream = null;
        try {
            inputStream = fs.open(oldPath);
            outputStream = fs.create(newPath);
            IOUtils.copyBytes(inputStream, outputStream, bufferSize, false);
        } finally {
            inputStream.close();
            outputStream.close();
            fs.close();
        }
    }
    //下载HDFS文件
    @Override
    public ResponseEntity<InputStreamResource> downloadFile(String path, String fileName) throws InterruptedException, IOException, URISyntaxException {
        if(StringUtils.isEmpty(path) ||StringUtils.isEmpty(fileName) ){
            return null;
        }
        FileSystem fs = getFileSystem();

        Path p = new Path(path);
        FSDataInputStream inputStream = fs.open(p);
        return YcFileUtil.downloadFile(inputStream,fileName);
    }
    //打开HDFS上的文件并返回byte数组
    @Override
    public byte[] openFileToBytes(String path) throws InterruptedException, IOException, URISyntaxException {
        if(StringUtils.isEmpty(path)){
            return null;
        }
        if(!existFile(path)){
            return null;
        }
        byte[] result = null;
        FileSystem fs = getFileSystem();

        //目标路径
        Path srcpath = new Path(path);
        try {
            FSDataInputStream inputStream =fs.open(srcpath);
            InputStream iis = inputStream.getWrappedStream(); //?
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[]bs = new byte[10*1024];
            int length=0;
            while((length=iis.read(bs,0,bs.length))!=-1){
                baos.write(bs,0,length);
            }
            baos.flush();
            result=baos.toByteArray();
        } finally {
            fs.close();
        }
        return result;
    }
    //上传HDFS文件
    @Override
    public void uploadFile(String path, String uploadPath) throws InterruptedException, IOException, URISyntaxException {
        if(StringUtils.isEmpty(path) ||StringUtils.isEmpty(uploadPath) ){
            return ;
        }
        FileSystem fs = getFileSystem();
        //上传路径
        Path clientpath = new Path(path);
        //目标路径
        Path serverPath = new Path(uploadPath);
        //调用文件系统的文件赋值方法，第一个参数是否删除源文件
        //true为删除 默认为false

        fs.copyFromLocalFile(false,clientpath,serverPath);
        fs.close();
    }

    //读取HDFS文件内容
    @Override
    public String readFile(String path) throws InterruptedException, IOException, URISyntaxException {
        if(StringUtils.isEmpty(path)  ){
            return null ;
        }
        if(!existFile(path)){
            return null;
        }
        FileSystem fs = getFileSystem();
        //目标路径
        Path srcPath = new Path(path);
        FSDataInputStream inputStream=null;
        try {

            inputStream=fs.open(srcPath);
            //防止中文乱码
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String lineTxt ="";
            StringBuffer sb = new StringBuffer();
            while((lineTxt=reader.readLine())!= null){
                sb.append(lineTxt);
            }
            return sb.toString();
        } finally {
            inputStream.close();
            fs.close();
        }
    }
    //创建文件
    @Override
    public void createFile(String path, MultipartFile file) throws Exception {
        if(StringUtils.isEmpty(path)  || file.getBytes()==null){
            return ;
        }
        String fileName=file.getOriginalFilename();//?
        FileSystem fs = getFileSystem();
        //上传时默认当前路径，后面自动拼接文件的目录
        Path newPath = null;
           if("/".equals(path)){
               newPath=   new Path(path+fileName);
           }else{
               newPath=   new Path(path+"/"+fileName);
           }
            //打开一个输出流
        FSDataOutputStream outputStream = fs.create(newPath);
        outputStream.write(file.getBytes());
        outputStream.close();
        fs.close();
    }
    //获取某个文件在HDFS的集群位置
    @Override
    public BlockLocation[] getFileBlockLocations(String path, MultipartFile file) throws Exception {
        if(StringUtils.isEmpty(path)  ){
            return  null;
        }
        if(!existFile(path)){
            return  null;
        }
        FileSystem fs = getFileSystem();
        //上传时默认当前路径，后面自动拼接文件的目录
        Path srcPath = new Path(path);
        FileStatus fileStatus =fs.getFileStatus(srcPath);
        return fs.getFileBlockLocations(fileStatus,0,fileStatus.getLen());
    }

    @Override
    public ResponseEntity<byte[]> downloadDirectory(String path, String fileName) throws IOException {
        //1.获取对象
        ByteArrayOutputStream out =null;//字节数组输出流(内存)
        try {
            FileSystem fs = this.getFileSystem();
            out=new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(out); //压缩流
            compress(path,zos,fs);
            zos.close();
        }catch (Exception e){e.printStackTrace();}
            byte [] bs =out.toByteArray();
                out.close();
        return YcFileUtil.downloadDirectory(bs,YcFileUtil.genFileName());
    }

    @Override
    public Map<String, String> getConfigurationInfoAsMap() throws InterruptedException, IOException, URISyntaxException {
        FileSystem fileSystem = getFileSystem();
        Configuration conf = fileSystem.getConf();
        Iterator<Map.Entry<String,String>> iterator = (  Iterator<Map.Entry<String,String>>)conf.iterator();
        Map<String,String> map = new HashMap<String, String>();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            map.put(entry.getKey(),entry.getValue());
        }
        return map;
    }

    //压缩                         hdfs中待下载的目录            压缩流
    private void compress(String baseDir, ZipOutputStream zipOutputStream, FileSystem fs) {
        try {
            FileStatus[] fileStatuses = fs.listStatus(new Path(baseDir));
            String []strs = baseDir.split("/"); //  /A/B
            System.out.println("strs"+strs);
            //lastName代表路径最后的单词
            String lastName=strs[strs.length-1];
            for (int i=0;i<fileStatuses.length;i++){
                String name=fileStatuses[i].getPath().toString();
                System.out.println(name);
                name=name.substring(name.indexOf("/"+lastName)); //子目录名或文件名
                System.out.println(name);
                if(fileStatuses[i].isFile()){
                    //如果 baseDir下的一个文件是File，以流读取
                    Path path = fileStatuses[i].getPath();
                    FSDataInputStream inputStream =fs.open(path);
                    zipOutputStream.putNextEntry(new ZipEntry(name.substring(1)));//name.substring(1) 去掉/ 得文件名
                    IOUtils.copyBytes(inputStream,zipOutputStream,this.bufferSize);
                    inputStream.close();
                }else {
                    zipOutputStream.putNextEntry(new ZipEntry(fileStatuses[i].getPath().getName()+"/"));
                    //递归目录，查询到所有得文件和其他目录
                    compress(fileStatuses[i].getPath().toString(),zipOutputStream,fs);
                }
            }
        }catch (Exception e){e.printStackTrace();}


    }


}
