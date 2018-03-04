package CreepyCrawler.crawler.model.details;

/**
 * Created by i on 07.02.2018.
 */
public class Details {
    private ListingsDetailsResult listingsDetailsResult;
    private boolean success = true;

    public ListingsDetailsResult getListingsDetailsResult () {
        return listingsDetailsResult;
    }

    public void setListingsDetailsResult (ListingsDetailsResult listingsDetailsResult) {
        this.listingsDetailsResult = listingsDetailsResult;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
