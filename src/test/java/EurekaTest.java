import com.ksudi.microservice.configcenter.ConfigCenterClient;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.client.ClientFactory;
import com.netflix.client.config.IClientConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.Server;
import feign.Feign;
import feign.ribbon.LBClient;
import feign.ribbon.RibbonClient;

import java.io.IOException;
import java.util.Properties;

public class EurekaTest {

    public static void main(String[] argv) throws IOException {


//
//        ApplicationInfoManager.getInstance().setInstanceStatus(
//                InstanceInfo.InstanceStatus.UP);
//        String vipAddress = "GATEWAY";
//        InstanceInfo nextServerInfo = DiscoveryManager.getInstance()
//                .getDiscoveryClient()
//                .getNextServerFromEureka(vipAddress, false);


        Properties properties = new Properties();
        properties.setProperty("myclient.ribbon.NIWSServerListClassName", "com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList");
        properties.setProperty("myclient.ribbon.DeploymentContextBasedVipAddresses", "GATEWAY");
//        ConfigurationManager.loadPropertiesFromResources("eureka-client.properties");
        DiscoveryManager.getInstance().initComponent(
                new MyDataCenterInstanceConfig(),
                new DefaultEurekaClientConfig());
// get LoadBalancer instance from configuration, properties file
        DynamicServerListLoadBalancer lb = (DynamicServerListLoadBalancer) ClientFactory.getNamedLoadBalancer("config");
// use RandomRule 's RandomRule algorithm to get a random server from lb 's server list


        RandomRule randomRule = new RandomRule();
        Server randomAlgorithmServer = randomRule.choose(lb, null);

        RibbonClient client = RibbonClient.builder().lbClientFactory((clientName) -> {
            IClientConfig config = ClientFactory.getNamedConfig(clientName);
            return LBClient.create(lb, config);
        }).build();

        ConfigCenterClient service = Feign.builder().client(client)
//                .encoder(new JacksonEncoder())
//                .decoder(new JacksonDecoder())
                .target(ConfigCenterClient.class, "http://config");
// .target(LoadBalancingTarget.create(LBSClient.class, "http://myclient"));
        System.out.println(service.properties("tmsadmin-test"));
    }

}
