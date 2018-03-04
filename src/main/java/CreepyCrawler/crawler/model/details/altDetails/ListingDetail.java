package CreepyCrawler.crawler.model.details.altDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by i on 05.02.2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListingDetail {
    private String businessName;
    private String email;
    private String extraEmails;
    private String websiteURL;
    private String primaryCategory;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExtraEmails() {
        return extraEmails;
    }

    public void setExtraEmails(String extraEmails) {
        this.extraEmails = extraEmails;
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
}
