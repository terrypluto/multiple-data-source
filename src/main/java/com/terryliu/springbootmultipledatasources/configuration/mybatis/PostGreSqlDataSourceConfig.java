package com.terryliu.springbootmultipledatasources.configuration.mybatis;

import com.terryliu.springbootmultipledatasources.db.PgVectorDataSource;
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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description PostGreSql MyBatis数据源配置
 */
@Configuration(proxyBeanMethods = false)
@MapperScan(basePackages = PostGreSqlDataSourceConfig.MAPPER_PACKAGE, sqlSessionFactoryRef = PostGreSqlDataSourceConfig.SESSION_FACTORY_NAME)
public class PostGreSqlDataSourceConfig {
    static final String DATA_SOURCE_NAME = "postgresDataSource";
    static final String MAPPER_PACKAGE = "com.terryliu.springbootmultipledatasources.mapper.postgres";
    static final String SESSION_FACTORY_NAME = "postgresSqlSessionFactory";
    static final String TRANSACTION_MANAGER_NAME = "postgresTransactionManager";
    static final String SESSION_TEMPLATE_NANE = "postgresSqlSessionTemplate";
    static final String DATA_SOURCE_PROPERTY_PREFIX = "spring.datasource.postgres";
    static final String XML_PATH_PATTERN = "classpath:mappers/postgres/*Mapper.xml";
    static final String PROPERTY_NAME = "postgresProperties";

    @ConfigurationProperties(prefix = DATA_SOURCE_PROPERTY_PREFIX)
    @Bean(PROPERTY_NAME)
    public DataSourceProperties postgresProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = DATA_SOURCE_NAME)
    public DataSource postgresDataSource(@Qualifier(PROPERTY_NAME) DataSourceProperties properties) {
        HikariDataSource dataSource = createDataSource(properties, PgVectorDataSource.class);
        if (StringUtils.hasText(properties.getName())) {
            dataSource.setPoolName(properties.getName());
        }
        return dataSource;
    }

    protected <T extends DataSource> T createDataSource(DataSourceProperties properties, Class<T> type) {
        return properties.initializeDataSourceBuilder().type(type).build();
    }


    @Bean(name = SESSION_FACTORY_NAME)
    public SqlSessionFactory postgreSqlSessionFactory(@Qualifier(DATA_SOURCE_NAME) DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(XML_PATH_PATTERN));
        return bean.getObject();
    }

    @Bean(name = TRANSACTION_MANAGER_NAME)
    public DataSourceTransactionManager postgresTransactionManager(@Qualifier(DATA_SOURCE_NAME) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = SESSION_TEMPLATE_NANE)
    public SqlSessionTemplate postgreSqlSessionTemplate(@Qualifier(SESSION_FACTORY_NAME) SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
