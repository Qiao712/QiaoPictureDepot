package com.qiao.picturedepot.security;

import com.qiao.picturedepot.exception.handler.ApiError;
import com.qiao.picturedepot.util.ObjectUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), "未认证.");
        response.getWriter().write(ObjectUtil.object2Json(apiError));
    }
}
