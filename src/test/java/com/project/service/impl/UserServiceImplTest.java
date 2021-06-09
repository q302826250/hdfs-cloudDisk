package com.project.service.impl;

import com.project.service.UserService;
import com.project.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: zhangjuntao
 * @Date: 2021/6/2 - 06 - 02 - 20:13
 * @Description :com.project.service.impl
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    UserService userService;
    @Test
    void insert() {
        System.out.println(userService);
        UserVO uv = new UserVO();
        uv.setUname("b");
        uv.setUpwd("b");
        System.out.println(uv);
        uv= userService.insert(uv);
        System.out.println(uv);
    }

    @Test
    void isUnameValid() {
        boolean result =userService.isUnameValid("smith");
        System.out.println(result);
    }
}