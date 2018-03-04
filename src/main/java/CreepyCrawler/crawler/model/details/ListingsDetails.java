package CreepyCrawler.crawler.model.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by i on 05.02.2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListingsDetails {
    private ArrayList<ListingDetail> listingDetail;

    public ArrayList<ListingDetail> getListingDetail() {
        return listingDetail;
    }

    public void setListingDetail(ArrayList<ListingDetail> listingDetail) {
        this.listingDetail = listingDetail;
    }
}
