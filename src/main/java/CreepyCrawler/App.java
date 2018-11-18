package CreepyCrawler;

import CreepyCrawler.crawler.manager.DataCollector;
import CreepyCrawler.crawler.model.Result;
import CreepyCrawler.crawler.model.search.Search;
import CreepyCrawler.crawler.model.search.SearchListing;
import CreepyCrawler.db.ListingDAO;
import CreepyCrawler.reports.ExcelWriter;
import CreepyCrawler.reports.RecordManager;
import CreepyCrawler.ui.ExportToExcel;
import CreepyCrawler.ui.MainView;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import org.apache.poi.ss.usermodel.Workbook;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class App {
    private DataCollector collector = new DataCollector();
    private ArrayList<Result> results = new ArrayList<>();

    private int recordsWithEmailCounter;
    private int totalRecordsCounter;

    private Integer totalListingsNumber;
    private boolean recordWithoutEmailNeeded;

    private StreamResource resource;

    public void search(String location, String category, MainView mainView) {
        try {
            processJob(location, category);

            mainView.setInterrupted(true);
            mainView.getResultString().setValue("Results are saving to the Data Base...");
            mainView.getProgressBarLayout().setVisible(false);
            saveResultsToDb(results, category);

            mainView.getFilePathString().setValue("Results' file is downloading...");
            ExcelWriter excelWriter = new ExcelWriter();
            Workbook workbook = excelWriter.write(results, location, category);
            ExportToExcel exportToExcel = new ExportToExcel();
            resource = exportToExcel.downloadExcelFile(workbook, category + " " + location);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
            RecordManager.makeReportFile("log.txt");
            String logText = Calendar.getInstance().getTime().toString() + "\r\n" + e;
            RecordManager.writeReportRecord("log.txt", "\r\n" + logText);
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

    public void setRecordsWithEmailCounter(int recordsWithEmailCounter) {
        this.recordsWithEmailCounter = recordsWithEmailCounter;
    }

    public int getRecordsWithEmailCounter() {
        return recordsWithEmailCounter;
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

    void setTotalListingsNumber(Integer totalListingsNumber) {
        this.totalListingsNumber = totalListingsNumber;
    }

    public boolean isRecordWithoutEmailNeeded() {
        return recordWithoutEmailNeeded;
    }

    public void setRecordWithoutEmailNeeded(boolean recordWithoutEmailNeeded) {
        this.recordWithoutEmailNeeded = recordWithoutEmailNeeded;
    }

    public StreamResource getResource() {
        return resource;
    }
}