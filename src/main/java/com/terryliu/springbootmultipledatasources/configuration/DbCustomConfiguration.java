package com.terryliu.springbootmultipledatasources.configuration;

import com.terryliu.springbootmultipledatasources.constants.EnvironmentConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description Mybatis 缺省配置
 */
@Configuration(proxyBeanMethods = false)
@Slf4j
public class DbCustomConfiguration {
    @Value("${spring.profiles.active:test}")
    private String profile;
    @Bean
    public ConfigurationCustomizer myBatisConfiguration() {
        return configuration -> {
            configuration.setCacheEnabled(true);
            configuration.setDefaultExecutorType(ExecutorType.REUSE);
            configuration.setDefaultStatementTimeout(300);
            configuration.setLogPrefix("Nyquist Data DB ");
            configuration.setAutoMappingBehavior(AutoMappingBehavior.PARTIAL);
            if (EnvironmentConstants.DEV.equalsIgnoreCase(profile) || EnvironmentConstants.LOCAL.equalsIgnoreCase(profile)) {
                configuration.setLogImpl(StdOutImpl.class);
            } else {
                configuration.setLogImpl(Log4j2Impl.class);
            }
            configuration.setLocalCacheScope(LocalCacheScope.SESSION);
            configuration.setJdbcTypeForNull(JdbcType.OTHER);
            log.info("MyBatis Custom Configuration Bean created...");
        };
    }
}
