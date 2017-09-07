package com.ksudi.microservice.configcenter;

import feign.Param;
import feign.RequestLine;

/**
 * Created by ICE on 2016/12/10.
 */
public interface ConfigCenterClient {

    @RequestLine("GET /{name}.properties")
    String properties(@Param("name") String name);


}
