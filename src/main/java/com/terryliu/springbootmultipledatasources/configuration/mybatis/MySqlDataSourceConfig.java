package com.terryliu.springbootmultipledatasources.configuration.mybatis;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description MySql MyBatis配置
 */
@Configuration(proxyBeanMethods = false)
@MapperScan(basePackages = MySqlDataSourceConfig.MAPPER_PACKAGE, sqlSessionFactoryRef = MySqlDataSourceConfig.SESSION_FACTORY_NAME)
public class MySqlDataSourceConfig {
    static final String DATA_SOURCE_NAME = "mysqlDataSource";
    static final String MAPPER_PACKAGE = "com.terryliu.springbootmultipledatasources.mapper.mysql";
    static final String SESSION_FACTORY_NAME = "mysqlSqlSessionFactory";
    static final String TRANSACTION_MANAGER_NAME = "mysqlTransactionManager";
    static final String SESSION_TEMPLATE_NANE = "mysqlSqlSessionTemplate";
    static final String DATA_SOURCE_PROPERTY_PREFIX = "spring.datasource.mysql";
    static final String XML_PATH_PATTERN = "classpath:mappers/mysql/*Mapper.xml";

    static final String PROPERTY_NAME = "mysqlProperties";


    @ConfigurationProperties(prefix = DATA_SOURCE_PROPERTY_PREFIX)
    @Bean(PROPERTY_NAME)
    public DataSourceProperties mysqlProperties(){
        return new DataSourceProperties();
    }
    @Bean(name = DATA_SOURCE_NAME)
    @Primary
    public DataSource mysqlDataSource(@Qualifier(PROPERTY_NAME) DataSourceProperties dataSourceProperties) {
        HikariDataSource dataSource = createDataSource(dataSourceProperties, HikariDataSource.class);
        if (StringUtils.hasText(dataSourceProperties.getName())) {
            dataSource.setPoolName(dataSourceProperties.getName());
        }
        return dataSource;
    }

    protected <T extends DataSource> T createDataSource(DataSourceProperties properties, Class<T> type) {
        return properties.initializeDataSourceBuilder().type(type).build();
    }

    @Bean(name = SESSION_FACTORY_NAME)
    @Primary
    public SqlSessionFactory mysqlSqlSessionFactory(@Qualifier(DATA_SOURCE_NAME) DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(XML_PATH_PATTERN));
        return bean.getObject();
    }

    @Bean(name = TRANSACTION_MANAGER_NAME)
    @Primary
    public DataSourceTransactionManager mysqlTransactionManager(@Qualifier(DATA_SOURCE_NAME) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = SESSION_TEMPLATE_NANE)
    @Primary
    public SqlSessionTemplate mysqlSqlSessionTemplate(@Qualifier(SESSION_FACTORY_NAME) SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
