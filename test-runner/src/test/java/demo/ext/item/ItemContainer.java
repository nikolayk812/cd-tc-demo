package demo.ext.item;

import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;

import java.time.Duration;

public class ItemContainer extends GenericContainer<ItemContainer> {
    public ItemContainer(Network network, int port) {
        super("tc-demo/item:latest");
        addExposedPorts(port);
        withNetwork(network);
        withNetworkAliases("item-service");

        addEnv("SERVER_PORT", port + "");
        addEnv("EUREKASERVER_PORT", "8761");
        addEnv("EUREKASERVER_URI", "http://eureka:8761/eureka/");
        addEnv("REDIS_HOST", "redis");
        addEnv("REDIS_PORT", "6379");

        HttpWaitStrategy httpWaitStrategy = new HttpWaitStrategy();
        httpWaitStrategy.withStartupTimeout(Duration.ofMinutes(1));
        waitingFor(httpWaitStrategy.forPath("/actuator/health").forStatusCode(200));

        withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(" --- item --- ")));
    }
}
