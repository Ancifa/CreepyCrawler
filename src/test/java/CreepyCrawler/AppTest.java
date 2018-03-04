package CreepyCrawler;

import org.junit.Assert;
import org.junit.Test;

public class AppTest {

    @Test
    public void testIsNextPageNeeded()
    {
        App app = new App();

        app.setTotalListingsNumber(49);
        int pageNumber = 1;
        Assert.assertFalse(app.isNextPageNeeded(pageNumber));

        app.setTotalListingsNumber(50);
        Assert.assertFalse(app.isNextPageNeeded(pageNumber));

        app.setTotalListingsNumber(51);
        Assert.assertTrue(app.isNextPageNeeded(pageNumber));

        pageNumber++;
        Assert.assertFalse(app.isNextPageNeeded(pageNumber));

        app.setTotalListingsNumber(100);
        Assert.assertFalse(app.isNextPageNeeded(pageNumber));

        app.setTotalListingsNumber(101);
        Assert.assertTrue(app.isNextPageNeeded(pageNumber));
    }
}
