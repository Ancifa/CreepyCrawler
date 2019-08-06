package CreepyCrawler.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public class DbConnectorClient {
    private String serviceUrl;
    private RestTemplate restTemplate;

    public DbConnectorClient(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> isListingExists(Long listingId) {
        String urlString = serviceUrl + "get-listing";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlString)
                .queryParam("listingId", listingId);

        return restTemplate.exchange(builder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                String.class);
    }

    public List getEmails(List<Long> ids) {
        String urlString = serviceUrl + "get-emails";

        return restTemplate.postForObject(urlString, ids, List.class);
    }
}
