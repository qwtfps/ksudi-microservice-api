package com.ksudi.microservice.feign.support;

import com.ksudi.microservice.feign.support.Exception.CallExceptionDecoder;
import com.ksudi.microservice.feign.support.interceptor.LogInterceptor;
import com.ksudi.microservice.feign.support.log.FeignLoggerFactory;
import feign.Feign;
import feign.ribbon.RibbonClient;

public class FeignCommonBuilder {

    private RibbonClient ribbonClient;

    public FeignCommonBuilder(RibbonClient ribbonClient) {
        this.ribbonClient = ribbonClient;
    }

    public <T> T create(Class<T> apiType, String serviceId) {
        return Feign.builder().client(ribbonClient)
                .errorDecoder(CallExceptionDecoder.getInstance()).logger(FeignLoggerFactory.getFeignLogger())
                .requestInterceptor(LogInterceptor.getInstance())
                .target(apiType, serviceId.startsWith("http") ? serviceId : "http://".concat(serviceId));
    }

}
