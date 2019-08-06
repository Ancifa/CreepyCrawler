package CreepyCrawler;

import CreepyCrawler.client.DbConnectorClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class AppTestConfig {
    @Value("${dbconnector_service_url}")
    private String dbConnectorServiceUrl;

    @Bean
    public DbConnectorClient dbConnectorClient() {
        return new DbConnectorClient(dbConnectorServiceUrl);
    }
}
