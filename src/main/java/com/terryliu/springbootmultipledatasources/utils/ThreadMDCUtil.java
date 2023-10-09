package com.terryliu.springbootmultipledatasources.utils;

import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Nyquist Data Tech Team
 * @version 1.0.0
 * @date 2023/10/9
 * @description 封装MDC用于向线程池传递信息
 */
public class ThreadMDCUtil {
    /**
     * 包装Callable实例，将主线程的上下文信息传递给当任务线程
     *
     * @param callable Callable实例
     * @param context  用户信息上下文信息
     * @return 包装的Callable实例
     */
    public static <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            if (CollectionUtils.isEmpty(context)) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     * 包装Callable实例，将主线程的上下文信息传递给当任务线程
     *
     * @param runnable Runnable
     * @param context  用户信息上下文信息
     * @return 包装的Runnable实例
     */
    public static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     * 设置上下文信息到当前线程
     *
     * @param context 当前上下文实例
     */
    public static void setMDCContextMap(final Map<String, String> context) {
        if (CollectionUtils.isEmpty(context)) {
            MDC.clear();
        } else {
            MDC.setContextMap(context);
        }
    }
}
