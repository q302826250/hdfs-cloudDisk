package com.project.filter;
import com.project.auth.DemoAuthenticationFailureHandler;
import com.project.exception.ValidationCodeException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ValidateCodeFilter extends OncePerRequestFilter {
    private DemoAuthenticationFailureHandler demoAuthenticationFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException,IOException {
        if ("/doLogin.action".equals(httpServletRequest.getRequestURI()) && "post".equalsIgnoreCase(httpServletRequest.getMethod())){
            try {
                validate(httpServletRequest);
            }catch (ValidationCodeException e){
                demoAuthenticationFailureHandler.onAuthenticationFailure(httpServletRequest,httpServletResponse,e);
                return;
            }
        }
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
    private void validate(HttpServletRequest request) throws ServletRequestBindingException, ValidationCodeException {
        String validateCode= (String) request.getSession().getAttribute("validateCode");
        String imageCode=request.getParameter("imageCode");
        if (imageCode==null||"".equalsIgnoreCase(imageCode)){
            throw new ValidationCodeException("验证码不能为空");
        }
        if (!validateCode.equalsIgnoreCase(imageCode)){
            throw new ValidationCodeException("验证码不匹配");
        }
    }


    public DemoAuthenticationFailureHandler getDemoAuthenticationFailureHandler(){
        return demoAuthenticationFailureHandler;
    }
    public void setDemoAuthenticationFailureHandler(DemoAuthenticationFailureHandler demoAuthenticationFailureHandler){
        this.demoAuthenticationFailureHandler=demoAuthenticationFailureHandler;

    }

}
