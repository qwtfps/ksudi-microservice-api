package com.ksudi.microservice.config;

import com.google.common.collect.Maps;
import com.netflix.appinfo.EurekaAccept;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.transport.DefaultEurekaTransportConfig;
import com.netflix.discovery.shared.transport.EurekaTransportConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.*;
import org.springframework.util.ClassUtils;

import java.util.*;

public class SpringEurekaClientConfig implements EurekaClientConfig {
    private static final Logger logger = LoggerFactory.getLogger(SpringEurekaClientConfig.class);

    public static final String DEFAULT_NAMESPACE = "eureka.";
    public static final String DEFAULT_ZONE = "defaultZone";
    private static final int DEFAULT_EXECUTOR_THREAD_POOL_SIZE = 5;
    private static final String ARCHAIUS_DEPLOYMENT_ENVIRONMENT = "archaius.deployment.environment";
    private static final String TEST = "test";
    private static final String EUREKA_ENVIRONMENT = "eureka.environment";

    private static final DynamicPropertyFactory configInstance = DynamicPropertyFactory.getInstance();
    private static final DynamicStringProperty EUREKA_PROPS_FILE = DynamicPropertyFactory.getInstance()
            .getStringProperty("eureka.client.props", "eureka-client");

    private final String namespace;
    private final EurekaTransportConfig transportConfig;

    private AbstractEnvironment environment;

    public SpringEurekaClientConfig(Properties properties) {

        environment = new StandardEnvironment();
        Map<String, Object> map = Maps.newHashMap();

        properties.forEach((k, v) -> {
            map.put(k.toString(), v);
        });

        PropertySource propertySource = new MapPropertySource("properties", map);
        environment.getPropertySources().addFirst(propertySource);
        this.namespace = DEFAULT_NAMESPACE;
        init();
        this.transportConfig = new DefaultEurekaTransportConfig(namespace, configInstance);
    }


    public SpringEurekaClientConfig(AbstractEnvironment environment) {
        this(DEFAULT_NAMESPACE, environment);
    }

    public SpringEurekaClientConfig(String namespace, AbstractEnvironment environment) {
        this.namespace = namespace;
        this.environment = environment;
        init();
        this.transportConfig = new DefaultEurekaTransportConfig(namespace, configInstance);
    }

    private void init() {
        String env = ConfigurationManager.getConfigInstance().getString(EUREKA_ENVIRONMENT, TEST);
        ConfigurationManager.getConfigInstance().setProperty(ARCHAIUS_DEPLOYMENT_ENVIRONMENT, env);

        Properties property = new Properties();
        MutablePropertySources propertySources = environment.getPropertySources();
        Iterator<PropertySource<?>> iterator = propertySources.iterator();
        while (iterator.hasNext()) {
            PropertySource<?> item = iterator.next();
            if (ClassUtils.isAssignableValue(EnumerablePropertySource.class, item)) {
                String keys[] = ((EnumerablePropertySource) item).getPropertyNames();
                Arrays.asList(keys).stream()
                        .filter(key -> !property.containsKey(key))
                        .forEach(key -> property.put(key, item.getProperty(key).toString()));
            }
        }
        ConfigurationManager.loadProperties(property);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.netflix.discovery.EurekaClientConfig#getRegistryFetchIntervalSeconds
     * ()
     */
    @Override
    public int getRegistryFetchIntervalSeconds() {
        return environment.getProperty(
                namespace + "client.refresh.interval", Integer.class, 30);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#
     * getInstanceInfoReplicationIntervalSeconds()
     */
    @Override
    public int getInstanceInfoReplicationIntervalSeconds() {
        return environment.getProperty(
                namespace + "appinfo.replicate.interval", Integer.class, 30);
    }

    @Override
    public int getInitialInstanceInfoReplicationIntervalSeconds() {
        return environment.getProperty(
                namespace + "appinfo.initial.replicate.time", Integer.class, 40);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getDnsPollIntervalSeconds()
     */
    @Override
    public int getEurekaServiceUrlPollIntervalSeconds() {
        return environment.getProperty(
                namespace + "serviceUrlPollIntervalMs", Integer.class, 5 * 60 * 1000) / 1000;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getProxyHost()
     */
    @Override
    public String getProxyHost() {
        return environment.getProperty(
                namespace + "eurekaServer.proxyHost", String.class, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getProxyPort()
     */
    @Override
    public String getProxyPort() {
        return environment.getProperty(
                namespace + "eurekaServer.proxyPort", String.class, null);
    }

    @Override
    public String getProxyUserName() {
        return environment.getProperty(
                namespace + "eurekaServer.proxyUserName", String.class, null);
    }

    @Override
    public String getProxyPassword() {
        return environment.getProperty(
                namespace + "eurekaServer.proxyPassword", String.class, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#shouldGZipContent()
     */
    @Override
    public boolean shouldGZipContent() {
        return environment.getProperty(
                namespace + "eurekaServer.gzipContent", Boolean.class, true);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getDSServerReadTimeout()
     */
    @Override
    public int getEurekaServerReadTimeoutSeconds() {
        return environment.getProperty(
                namespace + "eurekaServer.readTimeout", Integer.class, 8);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getDSServerConnectTimeout()
     */
    @Override
    public int getEurekaServerConnectTimeoutSeconds() {
        return environment.getProperty(
                namespace + "eurekaServer.connectTimeout", Integer.class, 5);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getBackupRegistryImpl()
     */
    @Override
    public String getBackupRegistryImpl() {
        return environment.getProperty(namespace + "backupregistry", String.class,
                null);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.netflix.discovery.EurekaClientConfig#getDSServerTotalMaxConnections()
     */
    @Override
    public int getEurekaServerTotalConnections() {
        return environment.getProperty(
                namespace + "eurekaServer.maxTotalConnections", Integer.class, 200);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.netflix.discovery.EurekaClientConfig#getDSServerConnectionsPerHost()
     */
    @Override
    public int getEurekaServerTotalConnectionsPerHost() {
        return environment.getProperty(
                namespace + "eurekaServer.maxConnectionsPerHost", Integer.class, 50);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getDSServerURLContext()
     */
    @Override
    public String getEurekaServerURLContext() {
        return environment.getProperty(
                namespace + "eurekaServer.context", String.class,
                environment.getProperty(namespace + "context", String.class, null));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getDSServerPort()
     */
    @Override
    public String getEurekaServerPort() {
        return environment.getProperty(
                namespace + "eurekaServer.port", String.class,
                environment.getProperty(namespace + "port", String.class, null));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getDSServerDomain()
     */
    @Override
    public String getEurekaServerDNSName() {
        return environment.getProperty(
                namespace + "eurekaServer.domainName", String.class,
                environment
                        .getProperty(namespace + "domainName", String.class, null));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#shouldUseDns()
     */
    @Override
    public boolean shouldUseDnsForFetchingServiceUrls() {
        return environment.getProperty(namespace + "shouldUseDns", Boolean.class, false);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.netflix.discovery.EurekaClientConfig#getDiscoveryRegistrationEnabled
     * ()
     */
    @Override
    public boolean shouldRegisterWithEureka() {
        return environment.getProperty(
                namespace + "registration.enabled", Boolean.class, true);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#shouldPreferSameZoneDS()
     */
    @Override
    public boolean shouldPreferSameZoneEureka() {
        return environment.getProperty(namespace + "preferSameZone", Boolean.class,
                true);
    }

    @Override
    public boolean allowRedirects() {
        return environment.getProperty(namespace + "allowRedirects", Boolean.class, false);
    }

    /*
         * (non-Javadoc)
         *
         * @see com.netflix.discovery.EurekaClientConfig#shouldLogDeltaDiff()
         */
    @Override
    public boolean shouldLogDeltaDiff() {
        return environment.getProperty(
                namespace + "printDeltaFullDiff", Boolean.class, false);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#shouldDisableDelta()
     */
    @Override
    public boolean shouldDisableDelta() {
        return environment.getProperty(namespace + "disableDelta", Boolean.class,
                false);
    }

    @Override
    public String fetchRegistryForRemoteRegions() {
        return environment.getProperty(namespace + "fetchRemoteRegionsRegistry", String.class, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getRegion()
     */
    @Override
    public String getRegion() {
        return environment.getProperty(namespace + "region", String.class, environment.getProperty("eureka.region", String.class, "us-east-1"));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getAvailabilityZones()
     */
    @Override
    public String[] getAvailabilityZones(String region) {
        return environment
                .getProperty(
                        namespace + "" + region + ".availabilityZones", String.class,
                        DEFAULT_ZONE).split(",");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.netflix.discovery.EurekaClientConfig#getEurekaServerServiceUrls()
     */
    @Override
    public List<String> getEurekaServerServiceUrls(String myZone) {
        String serviceUrls = environment.getProperty(
                namespace + "serviceUrl." + myZone, String.class, null);
        if (serviceUrls == null || serviceUrls.isEmpty()) {
            serviceUrls = environment.getProperty(
                    namespace + "serviceUrl." + "default", String.class, null);

        }
        if (serviceUrls != null) {
            return Arrays.asList(serviceUrls.split(","));
        }

        return new ArrayList<String>();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.netflix.discovery.EurekaClientConfig#shouldFilterOnlyUpInstances()
     */
    @Override
    public boolean shouldFilterOnlyUpInstances() {
        return environment.getProperty(
                namespace + "shouldFilterOnlyUpInstances", Boolean.class, true);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.netflix.discovery.EurekaClientConfig#getEurekaConnectionIdleTimeout()
     */
    @Override
    public int getEurekaConnectionIdleTimeoutSeconds() {
        return environment.getProperty(
                namespace + "eurekaserver.connectionIdleTimeoutInSeconds", Integer.class, 30);
    }

    @Override
    public boolean shouldFetchRegistry() {
        return environment.getProperty(
                namespace + "shouldFetchRegistry", Boolean.class, true);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getRegistryRefreshSingleVipAddress()
     */
    @Override
    public String getRegistryRefreshSingleVipAddress() {
        return environment.getProperty(
                namespace + "registryRefreshSingleVipAddress", String.class, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getHeartbeatExecutorThreadPoolSize()
     */
    @Override
    public int getHeartbeatExecutorThreadPoolSize() {
        return environment.getProperty(
                namespace + "client.heartbeat.threadPoolSize", Integer.class, DEFAULT_EXECUTOR_THREAD_POOL_SIZE);
    }

    @Override
    public int getHeartbeatExecutorExponentialBackOffBound() {
        return environment.getProperty(
                namespace + "client.heartbeat.exponentialBackOffBound", Integer.class, 10);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.netflix.discovery.EurekaClientConfig#getCacheRefreshExecutorThreadPoolSize()
     */
    @Override
    public int getCacheRefreshExecutorThreadPoolSize() {
        return environment.getProperty(
                namespace + "client.cacheRefresh.threadPoolSize", Integer.class, DEFAULT_EXECUTOR_THREAD_POOL_SIZE);
    }

    @Override
    public int getCacheRefreshExecutorExponentialBackOffBound() {
        return environment.getProperty(
                namespace + "client.cacheRefresh.exponentialBackOffBound", Integer.class, 10);
    }

    @Override
    public String getDollarReplacement() {
        return environment.getProperty(
                namespace + "dollarReplacement", String.class, "_-");
    }

    @Override
    public String getEscapeCharReplacement() {
        return environment.getProperty(
                namespace + "escapeCharReplacement", String.class, "__");
    }

    @Override
    public boolean shouldOnDemandUpdateStatusChange() {
        return environment.getProperty(
                namespace + "shouldOnDemandUpdateStatusChange", Boolean.class, true);
    }

    @Override
    public String getEncoderName() {
        return environment.getProperty(
                namespace + "encoderName", String.class, null);
    }

    @Override
    public String getDecoderName() {
        return environment.getProperty(
                namespace + "decoderName", String.class, null);
    }

    @Override
    public String getClientDataAccept() {
        return environment.getProperty(
                namespace + "clientDataAccept", String.class, EurekaAccept.full.name());
    }

    @Override
    public String getExperimental(String name) {
        return environment.getProperty(namespace + "experimental." + name, String.class, null);
    }

    @Override
    public EurekaTransportConfig getTransportConfig() {
        return transportConfig;
    }
}
