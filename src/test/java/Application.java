import com.ksudi.microservice.feign.support.Exception.RemoteCallException;
import com.ksudi.microservice.configcenter.ConfigCenterClient;
import org.apache.log4j.BasicConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {


    public static void main(String[] argv) throws InterruptedException {
        BasicConfigurator.configure();
        ApplicationContext ctx = new ClassPathXmlApplicationContext("app-context.xml");


        while (true) {
            Thread.sleep(1000l);
            try {
                System.out.println(ctx.getBean(ConfigCenterClient.class).properties("tmsadmin-test"));
            } catch(RemoteCallException e) {
                e.printStackTrace();
            }
        }

    }

}
