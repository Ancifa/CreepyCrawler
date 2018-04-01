package CreepyCrawler.ui;

import CreepyCrawler.App;
import CreepyCrawler.reports.RecordManager;
import com.vaadin.annotations.Theme;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by i on 18.02.2018.
 */
@SpringUI
@Theme("myStyles_new")
public class MainView extends UI {
    private final static String SERVICE_DESCRIPTION_TEXT = "This service searches and collects emails<br>of businesses across the USA." +
            "<br>Just fill in category and location fields and click on \"Search\" button." +
            "<br>Data is provided by the \"Yellow Pages\"" +
            "<br><a href=\"https://www.yellowpages.com\" target=\"_blank\">https://www.yellowpages.com</a>";

    private TextField category;
    private TextField location;
    private RadioButtonGroup<String> searchTypeSwitcher;
    private Button searchButton;
    private HorizontalLayout progressBarLayout;
    private ProgressBar progressBar;
    private Label resultString;
    private Label filePathString;
    private MailBlock mailBlock;

    private boolean interrupted;
    private App app;
    private boolean recordWithoutEmailNeeded;

    @Override
    public void init(VaadinRequest vaadinRequest) {
        setPollInterval(100);
        buildLayout();
    }

    private void buildLayout() {
        VerticalLayout mainLayout = new VerticalLayout();

        VerticalLayout headerLayout = buildHeader();
        VerticalLayout searchLayout = buildSearchLayout();

        mailBlock = new MailBlock();
//        VerticalLayout emailLayout = mailBlock.buildMailBlock();
        VerticalLayout emailLayout = new VerticalLayout();

        mainLayout.addComponents(headerLayout, searchLayout, emailLayout);
        setContent(mainLayout);
    }

    private VerticalLayout buildHeader() {
        VerticalLayout headerLayout = new VerticalLayout();
        headerLayout.addStyleName("border-bottom");
        Label label = new Label("Creepy Crawler");
        label.setWidth(null);
        label.addStyleName("header-label");
        headerLayout.addComponent(label);
        headerLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        return headerLayout;
    }

    private VerticalLayout buildSearchLayout() {
        VerticalLayout searchLayout = new VerticalLayout();
        HorizontalLayout imageLayout = new HorizontalLayout();
        HorizontalLayout fieldsLayout = new HorizontalLayout();
        fieldsLayout.setWidth(null);
        progressBarLayout = new HorizontalLayout();
        HorizontalLayout resultsLayout = new HorizontalLayout();
        HorizontalLayout filePathLayout = new HorizontalLayout();

        progressBar = new ProgressBar(0.0f);
        progressBarLayout.addComponent(progressBar);
        progressBarLayout.setVisible(false);

        ThemeResource resource = new ThemeResource("img/yp.jpg");
        Image image = new Image("", resource);
        Label serviceDescription = new Label(SERVICE_DESCRIPTION_TEXT, ContentMode.HTML);
        imageLayout.addComponents(image, serviceDescription);
        imageLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        category = new TextField();
        category.setPlaceholder("Category or business");
        location = new TextField();
        location.setPlaceholder("Location");

        searchTypeSwitcher = new RadioButtonGroup<>();
        searchTypeSwitcher.setItems("With emails only", "All");
        searchTypeSwitcher.setSelectedItem("With emails only");
        searchTypeSwitcher.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        searchTypeSwitcher.addSelectionListener(singleSelectionEvent ->
                recordWithoutEmailNeeded = !"With emails only".equals(searchTypeSwitcher.getValue())
        );

        searchButton = new Button("Search");
        searchButton.addClickListener(clickEvent -> {
            if (location.getValue().isEmpty() || category.getValue().isEmpty()) {
                Notification.show("Please, fill in category and location fields.",
                        Notification.Type.WARNING_MESSAGE);
                return;
            }
            app = new App();
            app.setRecordWithoutEmailNeeded(recordWithoutEmailNeeded);
            interrupted = false;

            Thread barThread = new Thread(new BarThread());
            Thread searchThread = new Thread(new SearchThread(location.getValue(), category.getValue()));
            barThread.start();
            searchThread.start();
        });
        fieldsLayout.addComponents(category, location, searchTypeSwitcher, searchButton);
        fieldsLayout.setComponentAlignment(location, Alignment.MIDDLE_CENTER);

        resultString = new Label();
        resultsLayout.addComponent(resultString);

        filePathString = new Label();
        filePathLayout.addComponent(filePathString);

        searchLayout.addComponents(imageLayout, fieldsLayout, resultsLayout, progressBarLayout, filePathLayout);

        return searchLayout;
    }

    private class BarThread implements Runnable {
        @Override
        public void run() {
            String totalNumber = "";
            while (!interrupted) {
                category.setEnabled(false);
                location.setEnabled(false);
                searchTypeSwitcher.setEnabled(false);
                searchButton.setEnabled(false);
                if (app.getTotalListingsNumber() != null && app.getTotalListingsNumber() != 0
                        && app.getTotalRecordsCounter() != 0) {
                    totalNumber = " of " + String.valueOf(app.getTotalListingsNumber());
                    float progress = (float) app.getTotalRecordsCounter() / app.getTotalListingsNumber();
                    resultString.setValue(String.valueOf(app.getTotalRecordsCounter()) + totalNumber
                    + " (" + Math.round(progress * 100) + "%)");
                    progressBar.setValue(progress);
                }
                try {
                    Thread.sleep(900);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            progressBar.setValue(0.0f);
        }
    }

    private class SearchThread implements Runnable {

        private String locationValue;
        private String categoryValue;

        SearchThread(String location, String category) {
            this.locationValue = location;
            this.categoryValue = category;
        }

        @Override
        public void run() {
            app.setRecordsWithEmailCounter(0);
            app.setTotalRecordsCounter(0);
            resultString.setValue("");
            filePathString.setValue("");
            progressBarLayout.setVisible(true);

            String result = app.search(locationValue, categoryValue);
            interrupted = true;

            progressBarLayout.setVisible(false);
            resultString.setValue(result);
            category.setEnabled(true);
            location.setEnabled(true);
            searchTypeSwitcher.setEnabled(true);
            searchTypeSwitcher.setSelectedItem("With emails only");
            searchButton.setEnabled(true);

            try {
                filePathString.setValue("File is at " + RecordManager.getReportsDirPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}