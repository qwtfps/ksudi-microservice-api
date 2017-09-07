package com.ksudi.microservice.feign.support.log;

import feign.Logger;
import org.slf4j.LoggerFactory;

public class FeignLogger extends Logger {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FeignLogger.class);


    @Override
    protected void log(String configKey, String format, Object... args) {
        logger.info(String.format(methodTag(configKey) + format, args));
    }
}
