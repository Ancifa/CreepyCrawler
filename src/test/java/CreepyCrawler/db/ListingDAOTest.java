package CreepyCrawler.db;

import CreepyCrawler.AppTestConfig;
import CreepyCrawler.client.DbConnectorClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.Map;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppTestConfig.class)
@SpringBootTest()
public class ListingDAOTest {
    @Autowired
    private DbConnectorClient dbConnectorClient;

    @Test
    public void testListingsAndEmailsAmount() throws SQLException, ClassNotFoundException {
        ListingDAO listingDAO = new ListingDAO();
        Map<String, Integer> listingsAndEmailsAmount = listingDAO.getListingsAndEmailsAmount();

        System.out.println(listingsAndEmailsAmount.toString());
        Assert.assertTrue(listingsAndEmailsAmount.size() > 0);
    }

    @Test
    public void testIsListingNotExistsWithoutDbConnector() throws Exception {
        ListingDAO listingDAO = new ListingDAO();
        Assert.assertFalse(listingDAO.isListingNotExists("466235186"));
        Assert.assertTrue(listingDAO.isListingNotExists("0"));
    }

    @Test
    public void testIsListingNotExistsWithDbConnector() throws Exception {
        ListingDAO listingDAO = new ListingDAO(dbConnectorClient);
        Assert.assertFalse(listingDAO.isListingNotExists("466235186"));
        Assert.assertTrue(listingDAO.isListingNotExists("0"));
    }
}