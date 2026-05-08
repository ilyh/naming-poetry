package com.example.naming.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BlacklistConfig.class)
public class BlacklistPropertiesConfig {
}