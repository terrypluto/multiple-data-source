package com.terryliu.springbootmultipledatasources.configuration.mybatis;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description AbstractMybatisConfig
 */
public abstract class AbstractMybatisConfig {
    protected <T extends DataSource> T createDataSource(DataSourceProperties properties, Class<T> type) {
        return properties.initializeDataSourceBuilder().type(type).build();
    }
}
