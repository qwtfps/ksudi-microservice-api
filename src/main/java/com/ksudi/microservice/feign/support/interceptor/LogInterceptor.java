package com.ksudi.microservice.feign.support.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogInterceptor implements RequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    private static class LazyHolder {
        private static final LogInterceptor singleInstacne = new LogInterceptor();
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        logger.info("request url is {}", requestTemplate.url());
    }

    public static LogInterceptor getInstance() {
        return LazyHolder.singleInstacne;
    }
}
