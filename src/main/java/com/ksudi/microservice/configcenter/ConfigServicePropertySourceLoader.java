package com.ksudi.microservice.configcenter;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;


public class ConfigServicePropertySourceLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigServicePropertySourceLoader.class);


    private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();


    private static ConfigServicePropertySourceLoader instance = new ConfigServicePropertySourceLoader();


    private boolean alreadyLoad = false;


    public Properties locate(AbstractEnvironment environment, List<String> propertiesNames, ConfigCenterClient configCenterClient) throws IOException {

        if (alreadyLoad) {
            return null;
        }

        alreadyLoad = true;
        CompositePropertySource composite = new CompositePropertySource("configService");
        logger.info("propertiesNames is {}", StringUtils.join(propertiesNames, ","));

        Properties property = new Properties();

        for (String propertiesName : propertiesNames) {
            String properties = configCenterClient.properties(propertiesName);
            propertiesPersister.load(property, new StringReader(properties));
//            property.load(new StringReader(properties));
            HashMap<String, Object> map = new HashMap<>();
            property.forEach((k, v) -> map.put(k.toString(), v));

            if (composite.getPropertySources().isEmpty()) {
                composite.addFirstPropertySource(new MapPropertySource(propertiesName, map));
            } else {
                composite.addPropertySource(new MapPropertySource(propertiesName, map));
            }
        }
        insertPropertySources(environment.getPropertySources(), composite);
        return property;
    }

    private void insertPropertySources(MutablePropertySources propertySources, CompositePropertySource composite) {
        MutablePropertySources incoming = new MutablePropertySources();
        incoming.addFirst(composite);


        boolean overrideNone = Boolean.valueOf(composite.getProperty("spring.cloud.config.overrideNone") == null ? "false" : "true");
        boolean allowOverride = Boolean.valueOf(composite.getProperty("spring.cloud.config.allowOverride") == null ? "true" : "false");
        boolean overrideSystemProperties = Boolean.valueOf(composite.getProperty("spring.cloud.config.overrideSystemProperties") == null ? "true" : "false");


        if (allowOverride && (overrideNone || !overrideSystemProperties)) {
            if (allowOverride) {
                propertySources.addLast(composite);
            } else {
                if (propertySources.contains("systemEnvironment")) {
                    if (!overrideSystemProperties) {
                        propertySources.addAfter("systemEnvironment", composite);
                    } else {
                        propertySources.addBefore("systemEnvironment", composite);
                    }
                } else {
                    propertySources.addLast(composite);
                }
            }
        } else {
            propertySources.addFirst(composite);
        }
    }

    public static ConfigServicePropertySourceLoader getInstance() {
        return instance;
    }

}
