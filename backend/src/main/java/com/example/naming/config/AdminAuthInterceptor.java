package com.example.naming.config;

import com.example.naming.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "X-Admin-Token";

    private final AdminAuthService adminAuthService;

    public AdminAuthInterceptor(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (!adminAuthService.isPasswordConfigured()) {
            writeError(response, HttpStatus.SERVICE_UNAVAILABLE,
                "后端尚未配置管理员密码，请设置环境变量 ADMIN_PASSWORD");
            return false;
        }

        if (!adminAuthService.hasValidSession(request.getHeader(TOKEN_HEADER))) {
            writeError(response, HttpStatus.UNAUTHORIZED, "管理员登录已失效，请重新验证");
            return false;
        }
        return true;
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
