package com.project.controller;

import com.project.service.UserService;
import com.project.util.YConstants;
import com.project.vo.JsonModel;
import com.project.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @Auther: zhangjuntao
 * @Date: 2021/4/29 - 04 - 29 - 19:30
 * @Description :${PACKAGE_NAME}
 * @Version: 1.0
 */
@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/isUnameValid.action",method ={ RequestMethod.GET,RequestMethod.POST})
    public JsonModel isUnameValid(String uname, JsonModel jm){ //这里的方法参数不是从界面上取得的，而是由SpringMVC常见后传入的空对象
        String regExp="^\\w{6,10}$";
        if(!uname.matches(regExp)){//区分大小写
            jm.setCode(0);
            jm.setMsg("用户名必须为6-10位以上数字字母下划线组成");
            return  jm;
        }
        boolean flag=userService.isUnameValid(uname);
        if(flag){
            jm.setCode(1);
        }else{
            jm.setCode(0);
            jm.setMsg("用户名重名");
        }
        return jm;
    }


    @RequestMapping(value = "/reg.action",method ={ RequestMethod.GET,RequestMethod.POST})
    public JsonModel reg(JsonModel jm, UserVO userVO){
        userVO =userService.insert(userVO);
        jm.setCode(1);
        return jm;
    }

    @RequestMapping(value = "/back/checkLogin",method ={ RequestMethod.GET,RequestMethod.POST})
    public JsonModel checkLoginOp(HttpSession session, JsonModel jm){ //这里的方法参数不是从界面上取得的，而是由SpringMVC常见后传入的空对象
        if(session.getAttribute(YConstants.LOGINUSER)==null){
            jm.setCode(0);
            jm.setMsg("用户没有登录");
        }else {
            jm.setCode(1);
            String  uname =  (String) session.getAttribute(YConstants.LOGINUSER);
            jm.setObj(uname);
        }
        return jm;
    }

}
