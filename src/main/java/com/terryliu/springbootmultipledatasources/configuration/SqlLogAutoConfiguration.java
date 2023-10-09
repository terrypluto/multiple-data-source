package com.terryliu.springbootmultipledatasources.configuration;

import com.terryliu.springbootmultipledatasources.interceptors.UpdateSqlInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description Monitor SQL log Configuration
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({MybatisAutoConfiguration.class, DbCustomConfiguration.class})
@Lazy(false)
public class SqlLogAutoConfiguration implements InitializingBean {
    private final List<SqlSessionFactory> sqlSessionFactoryList;

    public SqlLogAutoConfiguration(List<SqlSessionFactory> sqlSessionFactoryList) {
        this.sqlSessionFactoryList = sqlSessionFactoryList;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (CollectionUtils.isEmpty(sqlSessionFactoryList)) {
            return;
        }
        UpdateSqlInterceptor interceptor = new UpdateSqlInterceptor();
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
            if (!containsInterceptor(configuration, interceptor)) {
                interceptor.setConfiguration(configuration);
                configuration.addInterceptor(interceptor);
            }
        }

    }

    private boolean containsInterceptor(org.apache.ibatis.session.Configuration configuration, Interceptor interceptor) {
        try {
            return configuration.getInterceptors().contains(interceptor);
        } catch (Exception e) {
            return false;
        }
    }
}
