package CreepyCrawler.crawler.model;

import java.util.ArrayList;

/**
 * Created by i on 11.02.2018.
 */
public class Result {
    private String listingId;
    private String businessName;
    private ArrayList<String> emailsList;
    private String websiteURL;
    private String primaryCategory;
    private String city;
    private String state;
    private String services;

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public ArrayList<String> getEmailsList() {
        return emailsList;
    }

    public void setEmailsList(ArrayList<String> emailsList) {
        this.emailsList = emailsList;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

    public String getPrimaryCategory() {
        return primaryCategory;
    }

    public void setPrimaryCategory(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }
}
