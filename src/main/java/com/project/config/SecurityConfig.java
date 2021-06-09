package com.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.auth.DemoAuthenticationFailureHandler;
import com.project.filter.ValidateCodeFilter;
import com.project.service.impl.UserServiceImpl;
import com.project.util.YConstants;
import com.project.vo.JsonModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


//WebSecurityConfigurerAdapter 是SpringSecurity 提供的用于我们扩展自己的配置
@Configuration
@EnableWebSecurity
public class SecurityConfig  extends WebSecurityConfigurerAdapter {
    //该方法的主要用法
    //1.通过Java的方式 配置用户名/密码
    //2.在这里完成获得数据库中的用户信息
    //3.密码一定要加密（加密的方式一定要和注册是加密的方式一致）
    //4.登录认证
    @Autowired
    UserServiceImpl userService;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private DemoAuthenticationFailureHandler demoAuthenticationFailureHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        //Spring Security 提供了BCryptPasswordEncoder类,实现Spring的PasswordEncoder接口使用BCrypt强哈希方法来加密密码。
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

 //BCryptPasswordEncoder 用来对界面传过来的密码进行加密操作的

    //可以自定义加密方式，实现PasswordEncoder接口，
    // springsercurity后面的版本必须指定PasswordEncoder实现类，但如果不想加密的话，也可以通过空实现的方式
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new JWTPasswordEncoder();
//    }




    @Override//配置拦截模式 过滤资源
    protected void configure(HttpSecurity http) throws Exception {
        //http.addFilterBefore(verifyCodeFilter, UsernamePasswordAuthenticationFilter.class);
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        //错误处理组件
        validateCodeFilter.setDemoAuthenticationFailureHandler(demoAuthenticationFailureHandler);
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class).formLogin()
                //定义登录页面，未登录时，访问一个需要登录之后才能访问的接口，会自动跳转到该页面
                .loginPage("/login.html")//默认跳转的是springsecurity自带的登录界面
                //默认是 /login，但是当配置了.loginPage("/login.html")，默认值就变成了/login.html
                .loginProcessingUrl("/doLogin.action")
                // 设置登陆成功页
              //  .defaultSuccessUrl("/back/index.html")
                //定义登录时，用户名的 key，默认为 username
                .usernameParameter("uname") //要和前端name保持一致
//                定义登录时，用户密码的 key，默认为 password
                .passwordParameter("upwd")
          //      登录成功的处理器
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                        String uname = req.getParameter("uname");
                        HttpSession session = req.getSession();
                        session.setAttribute(YConstants.LOGINUSER,uname);
                        resp.sendRedirect("back/index.html");
                    }
                })
                //登录失败的处理器
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException exception) throws IOException, ServletException, IOException {
                        exception.printStackTrace();
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        out.write("登录失败，用户名或密码或验证码错误");
                        out.flush();
                    }
                })
                //和表单登录相关的接口统统都直接通过
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout.action")
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                        req.getSession().removeAttribute(YConstants.LOGINUSER); //从session 删除登录用户
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        JsonModel jm = new JsonModel();
                        jm.setCode(1);
                        //springMVC自带的json处理工具
                        String json =objectMapper.writeValueAsString(jm);
                        out.write(json);
                        out.flush();
                    }
                })
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .authorizeRequests()//开启登录配置
                // 如果有允许匿名的url，填在下面,这些url可以不经过过滤
                .antMatchers("/login.html", "/reg.html", "/verifyCodeServlet", "isUnameValid.action", "/reg.action").permitAll()
                //注意该处要与数据库的ROLE_后面部分保持一致，大小写也要一致
                .antMatchers("/back").hasRole("ADMIN")//表示访问 /back 这个接口，需要具备 ADMIN 这个角色
                .anyRequest().authenticated()//表示剩余的其他接口，任何用户登录之后就能访问
                .and()
                .csrf().disable();// 关闭CSRF跨域


    }


    /**
     * 直接过滤掉该地址，即该地址不走 Spring Security 过滤器链
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置拦截忽略文件夹，可以对静态资源放行
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }
}
