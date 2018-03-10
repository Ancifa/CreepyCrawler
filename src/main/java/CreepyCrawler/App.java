package CreepyCrawler;

import CreepyCrawler.crawler.manager.DataCollector;
import CreepyCrawler.crawler.manager.WebCrawler;
import CreepyCrawler.crawler.model.Result;
import CreepyCrawler.crawler.model.search.Search;
import CreepyCrawler.crawler.model.search.SearchListing;
import CreepyCrawler.db.ListingDAO;
import CreepyCrawler.reports.ExcelWriter;
import CreepyCrawler.reports.RecordManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class App {
    private String browsingUrl = "https://www.yellowpages.com/search?search_terms=it+company&geo_location_terms=San+Francisco%2C+CA";
    private String reportFileName = "results.xls";

    private DataCollector collector = new DataCollector();
    private ArrayList<Result> results = new ArrayList<>();

    private int recordsWithEmailCounter;
    private int totalRecordsCounter;

    private Integer totalListingsNumber;
    private boolean recordWithoutEmailNeeded;

    public String search(String location, String category) {
        long startTime;
        startTime = System.currentTimeMillis();
        try {
            processJob(location, category);
            saveResultsToDb(results, category);
            ExcelWriter excelWriter = new ExcelWriter();
            excelWriter.write(category + " " + location, results, location, category);
        } catch (Exception e) {
            e.printStackTrace();
            StringBuilder logText = new StringBuilder(Calendar.getInstance().getTime().toString());
            logText.append("\r\n");
            logText.append(e);
            RecordManager.makeReportFile("log.txt");
            RecordManager.writeReportRecord("log.txt", "\r\n" + logText.toString());
        } finally {
            return calcJobTime(startTime);
        }
    }

    private void processJob(String location, String category) throws Exception {
        int pageNumber = 0;
        do {
            pageNumber++;
            Search search = collector.sendSearchRequest(location, category, String.valueOf(pageNumber),
                    DataCollector.RequestFormat.JSON);
            totalListingsNumber = Integer.parseInt(search.getSearchResult().getMetaProperties().getTotalAvailable());
            ArrayList<SearchListing> listingsSearchList = search.getSearchResult().getSearchListings().getSearchListing();
            for (SearchListing searchListing : listingsSearchList) {
                collectData(searchListing);
            }
//        } while (false);
        } while (isNextPageNeeded(pageNumber));
    }

    boolean isNextPageNeeded(int pageNumber) {
        return totalListingsNumber > pageNumber * 50;
    }

    private void collectData(SearchListing searchListing) throws Exception {
        collectData(searchListing, DataCollector.RequestFormat.JSON);
    }

    private void collectData(SearchListing searchListing, DataCollector.RequestFormat format)
            throws Exception {
        Result result = collector.sendDetailsRequest(searchListing.getListingId(), format);
        collector.addListingDetails(result, searchListing);

        if (isEmailListNotEmpty(result) || recordWithoutEmailNeeded) {
            results.add(result);
            recordsWithEmailCounter++;
        }
        totalRecordsCounter++;
    }

    private void saveResultsToDb(ArrayList<Result> results, String category) throws SQLException, ClassNotFoundException {
        ListingDAO listingDAO = new ListingDAO();
        for (Result result : results) {
            listingDAO.saveListingToDb(result, category);
        }
    }

    private boolean isEmailListNotEmpty(Result result) {
        return result.getEmailsList().stream().anyMatch(a -> !"".equals(a));
    }

    private String calcJobTime(long startTime) {
        long endTime = System.currentTimeMillis();
        String workTime = (double) (endTime - startTime) / 1000 + " sec";
        String withEmails = isRecordWithoutEmailNeeded() ? "" : "with e-mails ";
        return recordsWithEmailCounter + " records " + withEmails + "of total "
                + totalListingsNumber + " records were found." + " Work time: " + workTime;
    }

    private void testingCrawler() {
        WebCrawler crawler = new WebCrawler();
        crawler.setUp();
        crawler.browseWebPage(browsingUrl, reportFileName);
        crawler.tearDown();
    }

    public int getRecordsWithEmailCounter() {
        return recordsWithEmailCounter;
    }

    public void setRecordsWithEmailCounter(int recordsWithEmailCounter) {
        this.recordsWithEmailCounter = recordsWithEmailCounter;
    }

    public int getTotalRecordsCounter() {
        return totalRecordsCounter;
    }

    public void setTotalRecordsCounter(int totalRecordsCounter) {
        this.totalRecordsCounter = totalRecordsCounter;
    }

    public Integer getTotalListingsNumber() {
        return totalListingsNumber;
    }

    public void setTotalListingsNumber(Integer totalListingsNumber) {
        this.totalListingsNumber = totalListingsNumber;
    }

    public boolean isRecordWithoutEmailNeeded() {
        return recordWithoutEmailNeeded;
    }

    public void setRecordWithoutEmailNeeded(boolean recordWithoutEmailNeeded) {
        this.recordWithoutEmailNeeded = recordWithoutEmailNeeded;
    }
}