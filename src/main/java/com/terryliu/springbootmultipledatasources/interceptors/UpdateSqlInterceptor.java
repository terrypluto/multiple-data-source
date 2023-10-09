package com.terryliu.springbootmultipledatasources.interceptors;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import com.terryliu.springbootmultipledatasources.utils.ThreadMDCUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.MDC;

import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description Intercept update sql and write to sql log file
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
@Slf4j
public class UpdateSqlInterceptor implements Interceptor {
    private Configuration configuration = null;
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN));
    private static final int ONE = 1;
    private static final Long ZERO = 0L;
    private static final Integer MAX_WAITING_COUNT = 2000;
    private final static ThreadPoolExecutor SQL_LOG_EXECUTOR = new ThreadPoolExecutor(ONE, ONE, ZERO, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(MAX_WAITING_COUNT), task -> new Thread(task, "Write-Log-Executor"),
            (r, executor) -> {
                WriteSqlLog logTask = (WriteSqlLog) r;
                log.info("[Ignore Log Record] sql:{}", logTask.getRawSql());
            });
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        long startTime = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            WriteSqlLog logTask = new WriteSqlLog(startTime, endTime, configuration, (StatementHandler) target);
            SQL_LOG_EXECUTOR.submit(ThreadMDCUtil.wrap(logTask, MDC.getCopyOfContextMap()));
        }
    }

    @AllArgsConstructor
    static class WriteSqlLog implements Runnable {
        private long startTime;
        private long endTime;
        private Configuration configuration;
        private StatementHandler statementHandler;

        @Override
        public void run() {
            String sql = this.getSql();
            if (StrUtil.isBlank(sql)) {
                return;
            }
            long sqlCost = endTime - startTime;
            log.info("sql:{}  cost_time:{} (ms)", sql, sqlCost);
        }

        /**
         * @return Raw Sql
         */
        public String getRawSql() {
            if (null == statementHandler) {
                return StrUtil.EMPTY;
            }
            BoundSql boundSql = statementHandler.getBoundSql();
            return boundSql.getSql();
        }

        /**
         * 获取sql
         */
        private String getSql() {
            try {
                BoundSql boundSql = statementHandler.getBoundSql();
                return formatSql(boundSql, configuration);
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("get sql error {}", statementHandler, e);
                }
            }
            return StrUtil.EMPTY;
        }

        /**
         * 获取完整的sql实体的信息
         */
        private String formatSql(BoundSql boundSql, Configuration configuration) {
            String sql = boundSql.getSql();
            if (sql == null || sql.isEmpty()) {
                return StrUtil.EMPTY;
            }
            if (configuration == null) {
                return StrUtil.EMPTY;
            }
            sql = beautifySql(sql);
            Object[] parameters = getParameters(boundSql);
            if (!match(sql, parameters)) {
                return sql;
            }
            sql = String.format(sql.replaceAll("\\?", "%s"), parameters);
            return sql;
        }

        /**
         * 获取SQL参数
         *
         * @return SQL 参数列表
         */
        private Object[] getParameters(BoundSql boundSql) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            Object parameterObject = boundSql.getParameterObject();
            List<String> parameters = new ArrayList<>();
            if (parameterMappings != null) {
                for (ParameterMapping parameterMapping : parameterMappings) {
                    if (parameterMapping.getMode() != ParameterMode.OUT) {
                        Object value;
                        String propertyName = parameterMapping.getProperty();
                        if (boundSql.hasAdditionalParameter(propertyName)) {
                            value = boundSql.getAdditionalParameter(propertyName);
                        } else if (parameterObject == null) {
                            value = null;
                        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                            value = parameterObject;
                        } else {
                            MetaObject metaObject = configuration.newMetaObject(parameterObject);
                            value = metaObject.getValue(propertyName);
                        }
                        String paramValueStr = "";
                        if (value instanceof String) {
                            paramValueStr = "'" + value + "'";
                        } else if (value instanceof Date) {
                            paramValueStr = "'" + DATE_FORMAT_THREAD_LOCAL.get().format(value) + "'";
                        } else {
                            paramValueStr = value + "";
                        }
                        if (!propertyName.contains("frch_criterion")) {
                            paramValueStr = paramValueStr;
                        }
                        parameters.add(paramValueStr);
                    }
                }
            }
            return parameters.toArray();
        }

        /**
         * 美化Sql
         */
        private String beautifySql(String sql) {
            sql = sql.replaceAll("[\\s\n ]+", " ");
            return sql;
        }

        /**
         * ? 和 参数的实际格式是否匹配
         *
         * @param sql    SQL 语句，可以带有 ? 的占位符
         * @param params 插入到 SQL 中的参数，可单个可多个可不填
         * @return 表示为 ? 和参数的实际个数匹配
         */
        private boolean match(String sql, Object[] params) {
            if (params == null || params.length == 0) {
                return true;
            }
            Matcher m = Pattern.compile("(\\?)").matcher(sql);
            int count = 0;
            while (m.find()) {
                count++;
            }
            return count == params.length;
        }
    }
}
