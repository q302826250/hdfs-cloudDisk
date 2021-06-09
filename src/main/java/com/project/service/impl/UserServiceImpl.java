package com.project.service.impl;

import com.project.bean.User;
import com.project.dao.UserDao;
import com.project.service.UserService;
import com.project.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@Transactional  //事务处理（添加事件）
//MySQL 默认开启自动提交
//重写UserDetailsService的loadUserByUsername方法
//UserService自定义接口（添加，用户名是否有效）
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    UserDao userDao;

    //springsecurity提供的一个密码加密类
    PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();


    @Override
    public UserDetails loadUserByUsername(String uname) throws UsernameNotFoundException {
        User u =new User();
        u.setUname(uname);
        Example<User> example = Example.of(u);
        Optional<User> optional = userDao.findOne(example);
        u=optional.orElseGet(new Supplier<User>() {
            @Override
            public User get() {
                return null;
            }
        });
        if (u==null){
            return null;
        }else {
            //创建一个权限的集合
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            //添加获取权限
            authorities.add(new SimpleGrantedAuthority(u.getRole()));
            //把对象信息（用户名，密码，权限）存入对象，返回该对象，controller层直接调用
            //如果数据库未加密需要添加以下注释的两行代码,但一般来说数据库是不能用明文来存密码的，太不安全了
            // org.springframework.security.core.userdetails.User user2 =new org.springframework.security.core.userdetails.User(user.getUsername(), passwordEncoder.encode(user.getPwd()), authorities);
            org.springframework.security.core.userdetails.User user2 =new org.springframework.security.core.userdetails.User(
                    u.getUname(), u.getUpwd(), authorities); //u.getUpwd()是数据库注册成功后的密码
            // System.out.println("管理员信息："+user.getUsername()+"   "+passwordEncoder.encode(user.getPwd())+"  "+user2.getAuthorities());
            return user2;
        }

    }

    @Override
    public UserVO insert(UserVO user) {
            User u = new User();
            u.setUname(user.getUname());
            u.setUpwd(passwordEncoder.encode(user.getUpwd()));
            u.setRole("ROLE_ADMIN");
        u = userDao.save(u); //po对象
        user.setUid(u.getUid()); //从po中取出数据库生成的uid存到vo中
        return user;
    }

    @Override
    public boolean isUnameValid(String uname) {
        User u= new User();
        u.setUname(uname);
        //Example条件对象
        Example<User> example = Example.of(u);
        Optional<User> optional = userDao.findOne(example);
        System.out.println("u1:"+u);
        u = optional.orElseGet(new Supplier<User>() {
            @Override
            public User get() {
                return null;
            }
        });
        if(u==null){
            return true;
        }
        return false;
    }
}

