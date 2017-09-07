package com.ksudi.microservice.config;

import com.ksudi.microservice.feign.support.FeignCommonBuilder;
import com.netflix.client.ClientFactory;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import feign.ribbon.LBClient;
import feign.ribbon.RibbonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.AbstractEnvironment;

import java.io.IOException;

@Configuration
@PropertySource("${microservice.api.config.path:classpath:eureka-client.properties}")
@SuppressWarnings("deprecation")
public class EurekaConfiguration {

    @Autowired
    private AbstractEnvironment environment;


    @Bean
    public EurekaClient eurekaClient() {
        return DiscoveryManagerWrap.getInstance(new SpringConfigCenterInstanceConfig(environment), new SpringEurekaClientConfig(environment));
    }

    @DependsOn("eurekaClient")
    @Bean
    public RibbonClient ribbonClient() throws IOException {
        return RibbonClient.builder().lbClientFactory((clientName) ->
                LBClient.create(ClientFactory.getNamedLoadBalancer(clientName), ClientFactory.getNamedConfig(clientName))
        ).build();
    }

    @Bean
    public FeignCommonBuilder feignCommonBuilder(@Autowired RibbonClient ribbonClient) {
        return new FeignCommonBuilder(ribbonClient);
    }


}
