package com.project.service;


import com.project.vo.UserVO;

/**
 * @Auther: zhangjuntao
 * @Date: 2021/5/4 - 05 - 04 - 18:42
 * @Description :com.yc.Biz.impl
 * @Version: 1.0
 */

public interface UserService {

    //添加用户
    public UserVO insert(UserVO user);

    //判断用户名是否有效果
    public boolean isUnameValid(String uname);
}
