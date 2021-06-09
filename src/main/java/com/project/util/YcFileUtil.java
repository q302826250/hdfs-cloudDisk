package com.project.util;

import org.apache.hadoop.fs.FSDataInputStream;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther: zhangjuntao
 * @Date: 2021/6/4 - 06 - 04 - 21:26
 * @Description :com.project.util
 * @Version: 1.0
 */
public class YcFileUtil {
    public static String formatTime(long time){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd hh:mm:sss");
        Date date = new Date();
        return sdf.format(date);
    }

    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024 ) {
            fileSizeString = df.format((double) fileS ) + "B";
        }
        else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        }else  {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static ResponseEntity<InputStreamResource> downloadFile(FSDataInputStream inputStream, String fileName) {
        try {
            byte[] testBytes = new byte[inputStream.available()];
            //http的响应协议
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control","no-cache,no-store,must-revalidate");//	指定请求和响应遵循的缓存机制
            //文件下载的方式：Content-Disposition:xxx.txt
            headers.add("Content-Disposition",String.format("attachment;filename=\"%s\"",fileName));
            headers.add("Pragma","no-cache");   //	用来包含实现特定的指令
            headers.add("EExpires","0");
            headers.add("Content-Language","UTF-8");
           //最终这句：让文件内容以流的形式输出

            return ResponseEntity.ok().headers(headers).contentLength(testBytes.length)
                    .contentType(MediaType.parseMediaType("application/octet-stream")).body(new InputStreamResource(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String genFileName() {
        Date d = new Date();
        DateFormat df  = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(d)+".zip";
    }

    public static ResponseEntity<byte[]> downloadDirectory(byte[] bs, String fileName) {
        try {
            //http的响应协议
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control","no-cache,no-store,must-revalidate");
            //文件下载的方式：Content-Disposition:xxx.txt
            headers.add("Content-Disposition",String.format("attachment;filename=\"%s\"",fileName));
            headers.add("Pragma","no-cache");
            headers.add("EExpires","0");
            headers.add("Content-Language","UTF-8");
            //最终这句：让文件内容以流的形式输出

            return ResponseEntity.ok().headers(headers).contentLength(bs.length)
                    .contentType(MediaType.parseMediaType("application/octet-stream")).body(bs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
