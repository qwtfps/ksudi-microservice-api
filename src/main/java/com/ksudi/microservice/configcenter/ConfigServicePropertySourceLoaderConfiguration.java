package com.ksudi.microservice.configcenter;

import com.ksudi.microservice.config.DiscoveryManagerWrap;
import com.ksudi.microservice.config.SpringConfigCenterInstanceConfig;
import com.ksudi.microservice.config.SpringEurekaClientConfig;
import com.ksudi.microservice.feign.support.Exception.CallExceptionDecoder;
import com.ksudi.microservice.feign.support.interceptor.LogInterceptor;
import com.ksudi.microservice.feign.support.log.FeignLoggerFactory;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import feign.Feign;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Configuration
public class ConfigServicePropertySourceLoaderConfiguration extends PropertyPlaceholderConfigurer implements ApplicationContextAware {


    @Value("${spring.cloud.properties.names}")
    private List<String> propertiesNames;

    private ApplicationContext applicationContext;

    private boolean isAlreadyLoadFromCloudConfig = false;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }


//    @EventListener(ContextRefreshedEvent.class)
//    public void createBean(ContextRefreshedEvent event) throws IOException {
//        ConfigServicePropertySourceLoader loader = ConfigServicePropertySourceLoader.getInstance();
//        loader.locate(environment, propertiesNames, configCenterClient);
//    }

    protected Properties mergeProperties() throws IOException {

        Properties result = new Properties();

        if (this.localOverride) {
            // Load properties from file upfront, to let local properties override.
            loadProperties(result);
        }

        if (this.localProperties != null) {
            for (Properties localProp : this.localProperties) {
                CollectionUtils.mergePropertiesIntoMap(localProp, result);
            }
        }

        if (!this.localOverride) {
            // Load properties from file afterwards, to let those properties override.
            loadProperties(result);
        }
        Object cloudPropertiesNames = result.get("spring.cloud.properties.names");

        if (cloudPropertiesNames != null && !isAlreadyLoadFromCloudConfig) {
            isAlreadyLoadFromCloudConfig = !isAlreadyLoadFromCloudConfig;
            List<String> propertiesNames = Arrays.asList(StringUtils.split(cloudPropertiesNames.toString(), ","));
            String path = result.getProperty("microservice.api.config.path");
            if (StringUtils.isEmpty(path)) {
                path = "classpath:eureka-client.properties";
            }
            Properties configProperties = new Properties();
            configProperties.load(applicationContext.getResource(path).getInputStream());
            EurekaClient eurekaClient = DiscoveryManagerWrap.getInstance(new SpringConfigCenterInstanceConfig(configProperties), new SpringEurekaClientConfig(configProperties));

            InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(configProperties.get("config.ribbon.DeploymentContextBasedVipAddresses").toString(), false);
            String url = String.format("http://%s:%s", instanceInfo.getIPAddr(), instanceInfo.getPort());
            ConfigCenterClient configCenterClient = Feign.builder()
                    .errorDecoder(CallExceptionDecoder.getInstance()).logger(FeignLoggerFactory.getFeignLogger())
                    .requestInterceptor(LogInterceptor.getInstance())
                    .target(ConfigCenterClient.class, url);

            Properties cloudProperties = new Properties();
            for (String propertiesName : propertiesNames) {
                String cloudString = configCenterClient.properties(propertiesName);
                cloudProperties.load(new StringReader(cloudString));
            }


            AbstractEnvironment environment = (AbstractEnvironment) applicationContext.getBean(Environment.class);
            ConfigServicePropertySourceLoader.getInstance().locate(environment, propertiesNames, configCenterClient);

            boolean overrideNone = Boolean.valueOf(cloudProperties.getProperty("spring.cloud.config.overrideNone") == null ? "false" : "true");
            boolean allowOverride = Boolean.valueOf(cloudProperties.getProperty("spring.cloud.config.allowOverride") == null ? "true" : "false");
            boolean overrideSystemProperties = Boolean.valueOf(cloudProperties.getProperty("spring.cloud.config.overrideSystemProperties") == null ? "true" : "false");


            if (allowOverride && (overrideNone || !overrideSystemProperties)) {

                cloudProperties.forEach((k, v) -> {
                    if (!result.contains(k)) {
                        result.put(k, v);
                    }
                });
            } else {
                cloudProperties.forEach((k, v) -> {
                    result.put(k, v);
                });
            }

        }
        return result;
    }


}
