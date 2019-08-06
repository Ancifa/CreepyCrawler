package CreepyCrawler;

import CreepyCrawler.client.DbConnectorClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class AppConfig {
    @Value("${dbconnector_service_url}")
    private String dbConnectorServiceUrl;

    @Bean
    public DbConnectorClient dbConnectorClient() {
        return new DbConnectorClient(dbConnectorServiceUrl);
    }
}
