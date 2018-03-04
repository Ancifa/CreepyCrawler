package CreepyCrawler.crawler.model.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by i on 05.02.2018.
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class ExtraEmails {
    private ArrayList<String> extraEmail;

    public ArrayList<String> getExtraEmail() {
        return extraEmail;
    }

    public void setExtraEmail(ArrayList<String> extraEmail) {
        this.extraEmail = extraEmail;
    }
}
