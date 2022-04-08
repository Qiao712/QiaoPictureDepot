package com.qiao.picturedepot.security;

import com.qiao.picturedepot.exception.handler.ApiError;
import com.qiao.picturedepot.util.ObjectUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN.value(), accessDeniedException.getMessage());
        response.getWriter().write(ObjectUtil.object2Json(apiError));
    }
}
