package com.project;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Auther: zhangjuntao
 * @Date: 2021/6/3 - 06 - 03 - 19:29
 * @Description :com.project
 * @Version: 1.0
 */
public class Test {
    public static void main(String[] args){
        String pass = "c";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        final String passHash = encoder.encode(pass);
        System.out.println(passHash);

        BCryptPasswordEncoder encoder2 = new BCryptPasswordEncoder();
        final boolean matches = encoder2.matches(pass, "$2a$10$aWj2NjfJPziJwZprF8faTevHYvoqfdJVMjX.qTHH6kecZL9nEkZGC");
        System.out.println(matches);
    }
}
