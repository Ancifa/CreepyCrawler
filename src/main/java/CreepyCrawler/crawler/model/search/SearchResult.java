package CreepyCrawler.crawler.model.search;

/**
 * Created by i on 11.02.2018.
 */
public class SearchResult {
    private MetaProperties metaProperties;
    private SearchListings searchListings;

    public MetaProperties getMetaProperties() {
        return metaProperties;
    }

    public void setMetaProperties(MetaProperties metaProperties) {
        this.metaProperties = metaProperties;
    }

    public SearchListings getSearchListings() {
        return searchListings;
    }

    public void setSearchListings(SearchListings searchListings) {
        this.searchListings = searchListings;
    }
}
