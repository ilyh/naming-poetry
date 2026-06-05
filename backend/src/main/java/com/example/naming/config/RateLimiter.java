package com.example.naming.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter implements Filter {
    private static final int MAX_REQUESTS_PER_MINUTE = 30;
    private static final long WINDOW_MS = 60_000;

    private final ConcurrentHashMap<String, RequestCounter> counters = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String ip = request.getRemoteAddr();
        RequestCounter counter = counters.computeIfAbsent(ip, k -> new RequestCounter());
        long now = System.currentTimeMillis();

        synchronized (counter) {
            if (now - counter.timestamp > WINDOW_MS) {
                counter.count = 0;
                counter.timestamp = now;
            }
            if (counter.count >= MAX_REQUESTS_PER_MINUTE) {
                ((HttpServletResponse) response).sendError(429, "请求过于频繁，请稍后再试");
                return;
            }
            counter.count++;
        }
        chain.doFilter(request, response);
    }

    private static class RequestCounter {
        int count = 0;
        long timestamp = System.currentTimeMillis();
    }

    @Configuration
    static class RateLimiterConfig {
        @Bean
        FilterRegistrationBean<RateLimiter> rateLimiterFilter() {
            FilterRegistrationBean<RateLimiter> bean = new FilterRegistrationBean<>();
            bean.setFilter(new RateLimiter());
            bean.addUrlPatterns("/api/*");
            bean.setName("rateLimiter");
            return bean;
        }
    }
}
