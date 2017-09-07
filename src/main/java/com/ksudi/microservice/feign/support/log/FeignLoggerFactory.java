package com.ksudi.microservice.feign.support.log;

public class FeignLoggerFactory {

    private static class LazyHolder {
        private static final FeignLogger feignLogger = new FeignLogger();
    }

    public static final FeignLogger getFeignLogger() {
        return LazyHolder.feignLogger;
    }
}
