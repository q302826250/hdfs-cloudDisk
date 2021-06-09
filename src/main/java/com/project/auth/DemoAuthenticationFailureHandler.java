package com.project.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * @Auther: zhangjuntao
 * @Date: 2021/6/3 - 06 - 03 - 15:00
 * @Description :com.project.config
 * @Version: 1.0
 */
@Component
public class DemoAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private Logger logger = LoggerFactory.getLogger(DemoAuthenticationFailureHandler.class);

    //JSON框架 转化成json格式输出
    //MVC自带
    @Autowired
    private ObjectMapper objectMapper;

//处理 登录失败的请求
    //e 用来封装错误信息对象的
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        logger.info("登录失败");
        //http :500
        httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        httpServletResponse.setContentType("application/json;charset=UFT-8");
        PrintWriter out = httpServletResponse.getWriter();
        //将异常输出成json
        String jsonResult = objectMapper.writeValueAsString(e.getMessage());
        out.write(jsonResult);
        out.flush();
    }
}
