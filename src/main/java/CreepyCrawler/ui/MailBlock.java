package CreepyCrawler.ui;

import CreepyCrawler.db.ListingDAO;
import CreepyCrawler.mail.MailObject;
import CreepyCrawler.mail.MailService;
import com.vaadin.ui.*;

import javax.mail.MessagingException;
import java.sql.SQLException;

class MailBlock {

    private ListingDAO listingDAO;

    private Label listingsRemains;
    private Label listingsTotal;
    private Label emailsRemains;
    private Label emailsTotal;
    private ComboBox mailServices;
    private PasswordField passwordField;
    private Button button;
    private ProgressBar progressBar;
    private Label results;
    private VerticalLayout namesLayout;

    private boolean interrupted;

    MailBlock() {
        listingDAO = new ListingDAO();
    }

    VerticalLayout buildMailBlock() {
        VerticalLayout verticalLayout = new VerticalLayout();
        HorizontalLayout headerLayout = new HorizontalLayout();
        HorizontalLayout summaryLayout = new HorizontalLayout();
        HorizontalLayout fieldsLayout = new HorizontalLayout();
        HorizontalLayout resultsLayout = new HorizontalLayout();
        namesLayout = new VerticalLayout();

        Label label = new Label("Mail Service");
        headerLayout.addComponent(label);

        buildSummaryLayout(summaryLayout);

        mailServices = new ComboBox();
        mailServices.setItems(MailService.YAHOO_ADDRESS, MailService.GMAIL_ADDRESS);
        mailServices.setEmptySelectionAllowed(false);
        mailServices.setTextInputAllowed(false);
        mailServices.setSelectedItem(MailService.YAHOO_ADDRESS);
        mailServices.setWidth("210px");

        passwordField = new PasswordField();
        passwordField.setPlaceholder("Password");

        button = new Button("Start");
        button.addClickListener(clickEvent -> {
            if ("Start".equals(button.getCaption())) {
                onStartClickActions();
            } else {
                onStopClickActions();
            }
        });

        progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        fieldsLayout.addComponents(mailServices, passwordField, button, progressBar);

        results = new Label();
        resultsLayout.addComponent(results);

        verticalLayout.addComponents(headerLayout, summaryLayout, fieldsLayout, resultsLayout);

        return verticalLayout;
    }

    private void buildSummaryLayout(HorizontalLayout summaryLayout) {
        Label listingsLabel = new Label("Listings remain: ");
        Label middleLabel = new Label(" out of ");
        Label middleLabel2 = new Label(" out of ");
        Label emailsLabel = new Label(" Emails remain: ");
        listingsRemains = new Label();
        listingsTotal = new Label();
        emailsRemains = new Label();
        emailsTotal = new Label();
        mountSummaryValues();
        summaryLayout.addComponents(listingsLabel, listingsRemains, middleLabel, listingsTotal,
                emailsLabel, emailsRemains, middleLabel2, emailsTotal);
    }

    public void mountSummaryValues() {
        try {
            listingsRemains.setValue(listingDAO.getListingsAndEmailsAmount().get("listings_remains").toString());
            listingsTotal.setValue(listingDAO.getListingsAndEmailsAmount().get("listings_total").toString());
            emailsRemains.setValue(listingDAO.getListingsAndEmailsAmount().get("emails_remains").toString());
            emailsTotal.setValue(listingDAO.getListingsAndEmailsAmount().get("emails_total").toString());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onStartClickActions() {
        if ("".equals(passwordField.getValue())) {
            Notification.show("Please, fill in the password field.",
                    Notification.Type.WARNING_MESSAGE);
            return;
        }

        Thread sendingEmailThread = new Thread(new SendingEmailThread());
        sendingEmailThread.start();
    }

    private void onStopClickActions() {
        interrupted = true;
        button.setCaption("Start");
    }

    private class SendingEmailThread implements Runnable {

        @Override
        public void run() {
            progressBar.setVisible(true);
            button.setCaption("Stop");
            results.setValue("");
            namesLayout.removeAllComponents();

            MailService mailService = new MailService((String) mailServices.getValue(), passwordField.getValue());

            MailObject mailObject;
            int count = 0;

            do {
                try {
                    mailObject = listingDAO.getFirstNotSentEmail();
                    if (mailObject != null) {
                        listingDAO.updateSendStatus(mailObject.getId());
                        mailService.sendEmail(mailObject.getEmails(), mailObject.getBusinessName());
                        count++;
                        namesLayout.addComponent(new Label(count + ". Sent OK to " + mailObject.getBusinessName()));
                    }
                } catch (MessagingException | SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    results.setValue(e.getMessage());
                }
            } while (!interrupted && count < 10);

            interrupted = false;
            progressBar.setVisible(false);
            button.setCaption("Start");
            results.setValue(count + " emails sent.");
            mountSummaryValues();
        }
    }
}