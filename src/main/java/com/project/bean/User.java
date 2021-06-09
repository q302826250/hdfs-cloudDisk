package com.project.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author 涛大爷的笔记本
 * @Auther: zhangjuntao
 * @Date: 2021/5/27 - 05 - 27 - 20:46
 * @Description :com.project.Bean
 * @Version: 1.0
 */
@Data
@Entity //对应的数据库 user
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) //产生json时是否忽略哪些字段
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uid;
    private String uname;
    private String upwd;
    private String role;

}
