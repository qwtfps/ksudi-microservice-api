package com.ksudi.microservice.config;

import com.google.common.collect.Maps;
import com.google.inject.ProvidedBy;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.PropertiesInstanceConfig;
import com.netflix.appinfo.providers.MyDataCenterInstanceConfigProvider;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.*;
import org.springframework.util.ClassUtils;

import javax.inject.Singleton;
import java.util.*;

@Singleton
@ProvidedBy(MyDataCenterInstanceConfigProvider.class)
public class SpringConfigCenterInstanceConfig extends PropertiesInstanceConfig implements EurekaInstanceConfig {

    private static final String TEST = "test";
    private static final String ARCHAIUS_DEPLOYMENT_ENVIRONMENT = "archaius.deployment.environment";
    private static final String EUREKA_ENVIRONMENT = "eureka.environment";
    private static final String APP_GROUP_ENV_VAR_NAME = "NETFLIX_APP_GROUP";
    private static final Logger logger = LoggerFactory.getLogger(PropertiesInstanceConfig.class);
    protected String namespace = "eureka.";
    private static final DynamicStringProperty EUREKA_PROPS_FILE = DynamicPropertyFactory
            .getInstance().getStringProperty("eureka.client.props", "eureka-client");
    private static final String UNKNOWN_APPLICATION = "unknown";

    private static final String DEFAULT_STATUSPAGE_URLPATH = "/Status";
    private static final String DEFAULT_HOMEPAGE_URLPATH = "/";
    private static final String DEFAULT_HEALTHCHECK_URLPATH = "/healthcheck";

    private String propSecurePort = namespace + "securePort";
    private String propSecurePortEnabled = propSecurePort + ".enabled";
    private String propNonSecurePort;
    private String idPropName;
    private String propName;
    private String propPortEnabled;
    private String propLeaseRenewalIntervalInSeconds;
    private String propLeaseExpirationDurationInSeconds;
    private String propSecureVirtualHostname;
    private String propVirtualHostname;
    private String propMetadataNamespace;
    private String propASGName;
    private String propAppGroupName;
    private String appGrpNameFromEnv;

    private AbstractEnvironment environment;


    public SpringConfigCenterInstanceConfig(Properties properties) {

        environment = new StandardEnvironment();
        Map<String, Object> map = Maps.newHashMap();

        properties.forEach((k, v) -> {
            map.put(k.toString(), v);
        });

        PropertySource propertySource = new MapPropertySource("properties", map);
        environment.getPropertySources().addFirst(propertySource);
        init(namespace);
    }


    public SpringConfigCenterInstanceConfig(AbstractEnvironment environment) {
        this.environment = environment;
        init(namespace);
    }

    public SpringConfigCenterInstanceConfig(String namespace, DataCenterInfo info, AbstractEnvironment environment) {
        super(namespace, info);
        this.environment = environment;
    }

    public SpringConfigCenterInstanceConfig(String namespace, AbstractEnvironment environment) {
        super(namespace);
        this.environment = environment;
    }


    /*
     * (non-Javadoc)
     *
     * @see com.netflix.appinfo.AbstractInstanceConfig#isInstanceEnabledOnit()
     */
    @Override
    public boolean isInstanceEnabledOnit() {
        return environment.getProperty(namespace + "traffic.enabled", Boolean.class, super.isInstanceEnabledOnit());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.appinfo.AbstractInstanceConfig#getNonSecurePort()
     */
    @Override
    public int getNonSecurePort() {
        return environment.getProperty(propNonSecurePort, Integer.class, super.getNonSecurePort());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.appinfo.AbstractInstanceConfig#getSecurePort()
     */
    @Override
    public int getSecurePort() {
        return environment.getProperty(propSecurePort, Integer.class, super.getSecurePort());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.appinfo.AbstractInstanceConfig#isNonSecurePortEnabled()
     */
    @Override
    public boolean isNonSecurePortEnabled() {
        return environment.getProperty(propPortEnabled, Boolean.class, super.isNonSecurePortEnabled());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.appinfo.AbstractInstanceConfig#getSecurePortEnabled()
     */
    @Override
    public boolean getSecurePortEnabled() {
        return environment.getProperty(propSecurePortEnabled, Boolean.class, super.getSecurePortEnabled());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.netflix.appinfo.AbstractInstanceConfig#getLeaseRenewalIntervalInSeconds
     * ()
     */
    @Override
    public int getLeaseRenewalIntervalInSeconds() {
        return environment.getProperty(propLeaseRenewalIntervalInSeconds, Integer.class, super.getLeaseRenewalIntervalInSeconds());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.appinfo.AbstractInstanceConfig#
     * getLeaseExpirationDurationInSeconds()
     */
    @Override
    public int getLeaseExpirationDurationInSeconds() {
        return environment.getProperty(propLeaseExpirationDurationInSeconds, Integer.class, super.getLeaseExpirationDurationInSeconds());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.appinfo.AbstractInstanceConfig#getVirtualHostName()
     */
    @Override
    public String getVirtualHostName() {
        if (this.isNonSecurePortEnabled()) {
            return environment.getProperty(propVirtualHostname, String.class, super.getVirtualHostName());
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.netflix.appinfo.AbstractInstanceConfig#getSecureVirtualHostName()
     */
    @Override
    public String getSecureVirtualHostName() {
        if (this.getSecurePortEnabled()) {
            return environment.getProperty(propSecureVirtualHostname, String.class, super.getSecureVirtualHostName());
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.appinfo.AbstractInstanceConfig#getASGName()
     */
    @Override
    public String getASGName() {
        return environment.getProperty(propASGName, String.class, super.getASGName());
    }

    /**
     * Gets the metadata map associated with the instance. The properties that
     * will be looked up for this will be <code>namespace + ".metadata"</code>.
     * <p>
     * <p>
     * For instance, if the given namespace is <code>eureka.appinfo</code>, the
     * metadata keys are searched under the namespace
     * <code>eureka.appinfo.metadata</code>.
     * </p>
     */
    @Override
    public Map<String, String> getMetadataMap() {
        Map<String, String> metadataMap = new LinkedHashMap<>();

        String subsetPrefix = propMetadataNamespace.charAt(propMetadataNamespace.length() - 1) == '.'
                ? propMetadataNamespace.substring(0, propMetadataNamespace.length() - 1)
                : propMetadataNamespace;


        MutablePropertySources propertySources = environment.getPropertySources();
        Iterator<PropertySource<?>> iterator = propertySources.iterator();
        while (iterator.hasNext()) {
            PropertySource<?> item = iterator.next();
            if (ClassUtils.isAssignableValue(EnumerablePropertySource.class, item)) {
                String keys[] = ((EnumerablePropertySource) item).getPropertyNames();
                Arrays.asList(keys).stream()
                        .filter(key -> key.startsWith(subsetPrefix) && !metadataMap.containsKey(key))
                        .forEach(key -> metadataMap.put(key, item.getProperty(key).toString()));
            }
        }

        return metadataMap;
    }

    @Override
    public String getInstanceId() {
        String result = environment.getProperty(idPropName, String.class, null);
        return result == null ? null : result.trim();
    }

    @Override
    public String getAppname() {
        return environment.getProperty(propName, String.class, UNKNOWN_APPLICATION);
    }

    @Override
    public String getAppGroupName() {
        return environment.getProperty(propAppGroupName, String.class, appGrpNameFromEnv);
    }

    public String getIpAddress() {
        return super.getIpAddress();
    }


    @Override
    public String getStatusPageUrlPath() {
        return environment.getProperty(namespace + "statusPageUrlPath", String.class,
                DEFAULT_STATUSPAGE_URLPATH);
    }

    @Override
    public String getStatusPageUrl() {
        return environment.getProperty(namespace + "statusPageUrl", String.class, null)
                ;
    }


    @Override
    public String getHomePageUrlPath() {
        return environment.getProperty(namespace + "homePageUrlPath", String.class,
                DEFAULT_HOMEPAGE_URLPATH);
    }

    @Override
    public String getHomePageUrl() {
        return environment.getProperty(namespace + "homePageUrl", String.class, null)

                ;
    }

    @Override
    public String getHealthCheckUrlPath() {
        return environment.getProperty(namespace + "healthCheckUrlPath", String.class,
                DEFAULT_HEALTHCHECK_URLPATH);
    }

    @Override
    public String getHealthCheckUrl() {
        return environment.getProperty(namespace + "healthCheckUrl", String.class, null)
                ;
    }

    @Override
    public String getSecureHealthCheckUrl() {
        return environment.getProperty(namespace + "secureHealthCheckUrl", String.class,
                null);
    }

    @Override
    public String[] getDefaultAddressResolutionOrder() {
        String result = environment.getProperty(namespace + "defaultAddressResolutionOrder", String.class, null);
        return result == null ? new String[0] : result.split(",");
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }


    private void init(String namespace) {
        this.namespace = namespace;
        propSecurePort = namespace + "securePort";
        propSecurePortEnabled = propSecurePort + ".enabled";
        propNonSecurePort = namespace + "port";

        idPropName = namespace + "instanceId";
        propName = namespace + "name";
        propPortEnabled = propNonSecurePort + ".enabled";
        propLeaseRenewalIntervalInSeconds = namespace + "lease.renewalInterval";
        propLeaseExpirationDurationInSeconds = namespace + "lease.duration";
        propSecureVirtualHostname = namespace + "secureVipAddress";
        propVirtualHostname = namespace + "vipAddress";
        propMetadataNamespace = namespace + "metadata.";
        propASGName = namespace + "asgName";
        propAppGroupName = namespace + "appGroup";

        appGrpNameFromEnv = ConfigurationManager.getConfigInstance()
                .getString(APP_GROUP_ENV_VAR_NAME, UNKNOWN_APPLICATION);

        String env = ConfigurationManager.getConfigInstance().getString(EUREKA_ENVIRONMENT, TEST);
        ConfigurationManager.getConfigInstance().setProperty(ARCHAIUS_DEPLOYMENT_ENVIRONMENT, env);
//        String eurekaPropsFile = EUREKA_PROPS_FILE.get();
//        try {
//            ConfigurationManager.loadCascadedPropertiesFromResources(eurekaPropsFile);
//        } catch (IOException e) {
//            logger.warn(
//                    "Cannot find the properties specified : {}. This may be okay if there are other environment "
//                            + "specific properties or the configuration is installed with a different mechanism.",
//                    eurekaPropsFile);
//
//        }
    }
}
