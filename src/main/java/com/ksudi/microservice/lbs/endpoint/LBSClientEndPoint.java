package com.ksudi.microservice.lbs.endpoint;

import com.ksudi.microservice.feign.support.FeignCommonBuilder;
import com.ksudi.microservice.lbs.LBSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LBSClientEndPoint {


    @Bean
    public LBSClient create(@Autowired FeignCommonBuilder feignCommonBuilder) {
        return feignCommonBuilder.create(LBSClient.class, "myclient");
    }


}
