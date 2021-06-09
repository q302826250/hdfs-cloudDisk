package com.project.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Auther: zhangjuntao
 * @Date: 2021/6/3 - 06 - 03 - 15:25
 * @Description :com.project.Exception
 * @Version: 1.0
 */
public class ValidationCodeException  extends AuthenticationException {
    public ValidationCodeException(String msg){
         super(msg);}
}
