package com.project.vo;

import lombok.Data;

/**
 * @author 涛大爷的笔记本
 * @Auther: zhangjuntao
 * @Date: 2021/5/27 - 05 - 27 - 20:46
 * @Description :com.project.Bean
 * @Version: 1.0
 */
@Data
public class UserVO {
    private Integer uid;
    private String uname;
    private String upwd;
    private String role;

    private String imageCode; //界面上才有的验证码

}
