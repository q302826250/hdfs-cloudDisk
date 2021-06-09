package com.project.controller;

import com.project.service.HdfsService;
import com.project.vo.JsonModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/back/hdfs")
public class FileController {


    @Autowired
    private HdfsService hdfsService;

    @RequestMapping(value = "/downloadFile.action" ,method = RequestMethod.GET)
    public  ResponseEntity<InputStreamResource> downloadFile(@RequestParam("path") String path, @RequestParam("fileName") String fileName) throws InterruptedException, IOException, URISyntaxException {
        ResponseEntity<InputStreamResource> result=null;
        try {
            result =this.hdfsService.downloadFile(path,fileName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    @RequestMapping(value = "/downloadDirectory.action" ,method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadDirectory(@RequestParam("path") String path, @RequestParam("fileName") String fileName) throws InterruptedException, IOException, URISyntaxException {
        ResponseEntity<byte[]> result=null;
        try {
            result =this.hdfsService.downloadDirectory(path,fileName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    //读取文件列表
    @RequestMapping(value = "uploadData.action",method = RequestMethod.POST)
    @ResponseBody
    public JsonModel uploadData(@RequestParam("currentPath") String currentPath   , MultipartFile file, JsonModel jm) throws Exception {
       try {
           this.hdfsService.createFile(currentPath,file); //待优化，只要查询变化的一个文件，不必要查询所有
           List<Map<String,String>> list = hdfsService.listStatus(currentPath);
           jm.setObj(list);
       }catch (Exception e){
           e.printStackTrace();
            jm.setCode(0);
            jm.setMsg(e.getMessage());
       }

        return jm;
    }

}
