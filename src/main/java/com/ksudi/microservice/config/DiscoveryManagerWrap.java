package com.ksudi.microservice.config;

import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;

public class DiscoveryManagerWrap {

    private static boolean alreadyCreateEurekaClient = false;

    public static EurekaClient getInstance(EurekaInstanceConfig eurekaInstanceConfig, EurekaClientConfig eurekaClientConfig) {

        if (!alreadyCreateEurekaClient) {
            DiscoveryManager.getInstance().initComponent(
                    eurekaInstanceConfig,
                    eurekaClientConfig);
        }
        return DiscoveryManager.getInstance().getEurekaClient();
    }


}
