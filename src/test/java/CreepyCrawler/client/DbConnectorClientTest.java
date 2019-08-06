package CreepyCrawler.client;

import CreepyCrawler.AppTestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes= AppTestConfig.class)
@SpringBootTest()
public class DbConnectorClientTest {

    @Autowired
    private DbConnectorClient client;

    @Test
    public void testIsListingExists() {
        ResponseEntity<String> result = client.isListingExists(466235186L);
        Assert.assertEquals("1", result.getBody());

        result = client.isListingExists(0L);
        Assert.assertEquals("0", result.getBody());
    }

    @Test
    public void testSelectFromEmail() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        List res = client.getEmails(ids);

        System.out.println(res.get(0));
        Assert.assertEquals(3, res.size());
    }
}
