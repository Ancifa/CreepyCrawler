package CreepyCrawler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class AppTest {

    private int totalListingsNumber;
    private int pageNumber;
    private boolean result;

    public AppTest(int totalListingsNumber, int pageNumber, boolean result) {
        this.totalListingsNumber = totalListingsNumber;
        this.pageNumber = pageNumber;
        this.result = result;
    }

    @Parameterized.Parameters(name = "{index}: for totalListingsNumber = {0}" +
            " and pageNumber = {1} the result should be {2}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] {
                {49, 1, false},
                {50, 1, false},
                {51, 1, true},
                {51, 2, false},
                {100, 2, false},
                {101, 2, true}
        };

        return Arrays.asList(data);
    }

    @Test
    public void testIsNextPageNeeded() {
        App app = new App();
        app.setTotalListingsNumber(totalListingsNumber);

        Assert.assertEquals("Wrong result for the nextPageNeeded", result, app.isNextPageNeeded(pageNumber));
    }
}
