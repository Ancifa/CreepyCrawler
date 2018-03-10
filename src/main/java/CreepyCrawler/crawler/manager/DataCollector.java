package CreepyCrawler.crawler.manager;

import CreepyCrawler.crawler.model.Result;
import CreepyCrawler.crawler.model.details.Details;
import CreepyCrawler.crawler.model.details.ListingDetail;
import CreepyCrawler.crawler.model.details.ListingsDetailsResult;
import CreepyCrawler.crawler.model.search.Search;
import CreepyCrawler.crawler.model.search.SearchListing;
import CreepyCrawler.reports.RecordManager;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by i on 04.02.2018.
 */
public class DataCollector {
    private final static String REQUEST_URL = "http://pubapi.yp.com/search-api/search/devapi/";
    private final static String SEARCH_STRING = "search?";
    private final static String LOCATION_STRING = "searchloc=";
    private final static String TERM_STRING = "&term=";
    private final static String LISTING_COUNT_STRING = "&listingcount=";
    private final static String PAGE_NUMBER_STRING = "&pagenum=";
    private final static String DETAILS_STRING = "details?";
    private final static String LISTING_ID_STRING = "listingid=";
    private final static String KEY = "&key=v2htdck70f";

    public Result sendDetailsRequest(String listingId, RequestFormat format) throws IOException, JAXBException {
        StringBuilder urlString = new StringBuilder(REQUEST_URL);
        urlString.append(DETAILS_STRING)
                .append(LISTING_ID_STRING)
                .append(listingId);
        urlString.append(RequestFormat.JSON.equals(format)
                ? RequestFormat.JSON.getFormat() : RequestFormat.XML.getFormat());
        urlString.append(KEY);

        switch (format) {
            case XML:
                return fillResultsData(geXmlResponseForDetails(urlString.toString()));
            default:
                Details details = getJsonResponseForDetails(urlString.toString());
                if (details.isSuccess()) {
                    return fillResultsData(details);
                } else {
                    return fillResultsData(getJsonResponseForAltDetails(urlString.toString()));
                }
        }
    }

    private ListingsDetailsResult geXmlResponseForDetails(String urlString) throws IOException, JAXBException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        int code = connection.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        connection.disconnect();

        JAXBContext jc = JAXBContext.newInstance(ListingsDetailsResult.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        StreamSource streamSource = new StreamSource(new StringReader(response.toString()));
        JAXBElement<ListingsDetailsResult> je = unmarshaller.unmarshal(streamSource,
                ListingsDetailsResult.class);

        return je.getValue();
    }


    private Details getJsonResponseForDetails(String urlString) throws IOException, JAXBException {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForObject(urlString, Details.class);
        } catch (HttpMessageNotReadableException e) {
            Details details = new Details();
            details.setSuccess(false);
            return details;
        }
    }

    private CreepyCrawler.crawler.model.details.altDetails.Details getJsonResponseForAltDetails(String urlString)
            throws IOException, JAXBException {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(urlString, CreepyCrawler.crawler.model.details.altDetails.Details.class);
    }

    private Search getJsonResponseForSearch(String urlString) throws IOException, JAXBException {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(urlString, Search.class);
    }

    private Result fillResultsData(ListingsDetailsResult listingsDetailsResult) {
        Result result = new Result();
        ListingDetail listingDetail = listingsDetailsResult.getListingsDetails().getListingDetail().get(0);
        result.setBusinessName(listingDetail.getBusinessName());

        addEmails(result, listingDetail);

        result.setWebsiteURL(listingDetail.getWebsiteURL());
        result.setPrimaryCategory(listingDetail.getPrimaryCategory());

        return result;
    }

    private Result fillResultsData(Details listingsDetailsResult) {
        Result result = new Result();
        if (listingsDetailsResult != null && listingsDetailsResult.isSuccess()) {
            ListingDetail listingDetail = listingsDetailsResult.getListingsDetailsResult()
                    .getListingsDetails().getListingDetail().get(0);
            result.setBusinessName(listingDetail.getBusinessName());

            addEmails(result, listingDetail);

            result.setWebsiteURL(listingDetail.getWebsiteURL());
            result.setPrimaryCategory(listingDetail.getPrimaryCategory());
        }

        return result;
    }

    private void addEmails(Result result, ListingDetail listingDetail) {
        ArrayList<String> emailList = listingDetail.getExtraEmails().getExtraEmail();
        result.setEmailsList(emailList);
        if (!"".equals(listingDetail.getEmail())) {
            result.getEmailsList().add(listingDetail.getEmail());
        }
    }

    private Result fillResultsData(CreepyCrawler.crawler.model.details.altDetails.Details listingsDetailsResult) {
        Result result = new Result();
        if (listingsDetailsResult != null && listingsDetailsResult.isSuccess()) {
            CreepyCrawler.crawler.model.details.altDetails.ListingDetail listingDetail = listingsDetailsResult.getListingsDetailsResult()
                    .getListingsDetails().getListingDetail().get(0);
            result.setBusinessName(listingDetail.getBusinessName());
            result.setEmailsList(new ArrayList<>());
            if (!"".equals(listingDetail.getExtraEmails())) {
                result.getEmailsList().add(listingDetail.getExtraEmails());
            }
            if (!"".equals(listingDetail.getEmail())) {
                result.getEmailsList().add(listingDetail.getEmail());
            }
            result.setWebsiteURL(listingDetail.getWebsiteURL());
            result.setPrimaryCategory(listingDetail.getPrimaryCategory());
        }

        return result;
    }

    public Search sendSearchRequest(String location, String searchKeyword, String pageNumber,
                                    RequestFormat format) throws IOException, JAXBException {
        StringBuilder urlString = new StringBuilder(REQUEST_URL);
        urlString.append(SEARCH_STRING)
                .append(LOCATION_STRING)
                .append(convertString(location))
                .append(TERM_STRING)
                .append(convertString(searchKeyword))
                .append(LISTING_COUNT_STRING)
                .append("50")
                .append(PAGE_NUMBER_STRING)
                .append(pageNumber);
        urlString.append(RequestFormat.JSON.equals(format)
                ? RequestFormat.JSON.getFormat() : RequestFormat.XML.getFormat());
        urlString.append(KEY);

        return getJsonResponseForSearch(urlString.toString());
    }

    private String convertString(String primaryString) {
        if (primaryString.contains(" ")) {
            return primaryString.replaceAll(" ", "+");
        } else if (primaryString.contains("-")) {
            return primaryString.replaceAll("-", "+");
        } else {
            return primaryString;
        }
    }

    public void addListingDetails(Result result, SearchListing searchListing) {
        result.setListingId(searchListing.getListingId());
        result.setCity(searchListing.getCity());
        result.setState(searchListing.getState());
        result.setServices(searchListing.getServices());
        if (!"".equals(searchListing.getEmail())) {
            result.getEmailsList().add(searchListing.getEmail());
        }
    }

    public enum RequestFormat {
        JSON("&format=json"),
        XML("&format=xml");

        private String format;

        RequestFormat(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }
    }
}