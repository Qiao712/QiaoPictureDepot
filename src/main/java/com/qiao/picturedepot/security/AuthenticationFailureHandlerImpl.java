package com.qiao.picturedepot.security;

import com.qiao.picturedepot.pojo.dto.ApiError;
import com.qiao.picturedepot.util.ObjectUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN.value(), "认证失败: " + exception.getMessage());
        response.getWriter().write(ObjectUtil.object2Json(apiError));
    }
}
