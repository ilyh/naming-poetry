package com.example.naming.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
public class EncodingConfig {

    @Bean
    public org.springframework.boot.env.PropertySourceLoader propertySourceLoader() {
        return new YamlPropertySourceLoader() {
            @Override
            public PropertySource<?> load(String name, org.springframework.core.io.Resource resource) throws IOException {
                // 确保使用UTF-8编码读取配置
                if (resource instanceof EncodedResource) {
                    ((EncodedResource) resource).setEncoding(StandardCharsets.UTF_8);
                }
                return super.load(name, resource);
            }
        };
    }
}