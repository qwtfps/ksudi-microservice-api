package com.ksudi.microservice.configcenter.endpoint;

import com.ksudi.microservice.feign.support.Exception.CallExceptionDecoder;
import com.ksudi.microservice.configcenter.ConfigCenterClient;
import com.ksudi.microservice.feign.support.FeignCommonBuilder;
import com.ksudi.microservice.feign.support.interceptor.LogInterceptor;
import com.ksudi.microservice.feign.support.log.FeignLoggerFactory;
import feign.Feign;
import feign.ribbon.RibbonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigCenterClientEndPoint {


    @Bean
    public ConfigCenterClient create(@Autowired FeignCommonBuilder feignCommonBuilder) {
        return feignCommonBuilder.create(ConfigCenterClient.class, "config");
    }


}
