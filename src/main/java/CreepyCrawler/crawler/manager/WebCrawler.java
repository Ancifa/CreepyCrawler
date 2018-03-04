package CreepyCrawler.crawler.manager;

import CreepyCrawler.reports.RecordManager;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by i on 15.01.2018.
 */
public class WebCrawler {

    private WebDriver driver;

//    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:/Drivers/chromedriver.exe");
        driver = new ChromeDriver();
    }

//    @Test
    public void browseWebPage(String url, String fileName) {
        driver.get(url);
        int pageNumber = 2;
        try {
            while (true) {
                List<WebElement> elementsList = driver.findElements(By.linkText("More Info"));
                for (WebElement element : elementsList) {
                    extractEmail(fileName, element);
                }
                WebElement paginator = driver.findElement(By.className("pagination"));
                WebElement page = paginator.findElement(By.linkText(String.valueOf(pageNumber)));
                page.click();
                Thread.sleep(10000);
//                WebDriverWait wait = new WebDriverWait(driver, 40);
//                wait.until(ExpectedConditions.presenceOfElementLocated(By.className("pagination")));
                pageNumber++;
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        } catch (StaleElementReferenceException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            tearDown();
            String msg = "\r\n" + (pageNumber - 1) + " pages processed.";
            RecordManager.writeReportRecord(fileName, msg);
        }
    }

    private void extractEmail(String fileName, WebElement element) {
        String newPageUrl = element.getAttribute("href");
        WebDriver secondDriver = new ChromeDriver();
        secondDriver.get(newPageUrl);
        try {
            WebElement infoBlock = secondDriver.findElement(By.className("business-card-footer"));
            WebElement mailBlock = infoBlock.findElement(By.className("email-business"));
            List<WebElement> elementsList = mailBlock.findElements(By.xpath("..//a"));
            for (WebElement el : elementsList) {
                if (el.getAttribute("href").startsWith("mailto:")) {
                    String emailRecord = el.getAttribute("href");
                    String[] editedString = emailRecord.split(":");
                    saveEmailToFile(fileName, editedString[1]);
                }
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        } finally {
            secondDriver.quit();
        }
    }

    private void saveEmailToFile(String fileName, String emailRecord) {
        RecordManager.writeReportRecord(fileName, emailRecord);
    }

    //    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

}
