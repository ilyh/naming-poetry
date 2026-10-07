package com.example.naming.controller;

import com.example.naming.service.AdminAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private static final String TOKEN_HEADER = "X-Admin-Token";

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/auth")
    public ResponseEntity<Map<String, Object>> login(@RequestBody(required = false) Map<String, String> body) {
        if (!adminAuthService.isPasswordConfigured()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "后端尚未配置管理员密码，请设置环境变量 ADMIN_PASSWORD"));
        }
        if (body == null || !adminAuthService.matchesPassword(body.get("password"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "管理员密码错误"));
        }

        return ResponseEntity.ok(Map.of(
            "token", adminAuthService.createSession(),
            "expiresInSeconds", adminAuthService.getSessionTtlSeconds()
        ));
    }

    @GetMapping("/session")
    public Map<String, Boolean> verifySession() {
        return Map.of("authenticated", true);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = TOKEN_HEADER, required = false) String token) {
        adminAuthService.revokeSession(token);
        return ResponseEntity.noContent().build();
    }
}
