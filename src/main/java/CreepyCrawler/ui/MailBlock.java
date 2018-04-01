package CreepyCrawler.ui;

import CreepyCrawler.db.ListingDAO;
import CreepyCrawler.mail.MailObject;
import CreepyCrawler.mail.MailService;
import com.vaadin.ui.*;
import org.apache.commons.exec.util.StringUtils;

import javax.mail.MessagingException;
import java.sql.SQLException;

public class MailBlock {

    private ComboBox mailServices;
    private PasswordField passwordField;
    private Button button;
    private ProgressBar progressBar;
    private Label results;

    private boolean interrupted;

    public VerticalLayout buildMailBlock() {
        VerticalLayout verticalLayout = new VerticalLayout();
        HorizontalLayout headerLayout = new HorizontalLayout();
        HorizontalLayout fieldsLayout = new HorizontalLayout();
        HorizontalLayout resultsLayout = new HorizontalLayout();

        Label label = new Label();
        label.setValue("Mail Service");
        headerLayout.addComponent(label);

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

        verticalLayout.addComponents(headerLayout, fieldsLayout, resultsLayout);

        return verticalLayout;
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

            MailService mailService = new MailService((String) mailServices.getValue(), passwordField.getValue());

            ListingDAO listingDAO = new ListingDAO();

            MailObject mailObject;
            int count = 0;

            do {
                try {
                    mailObject = listingDAO.getFirstNotSentEmail();
                    if (mailObject != null) {
                        listingDAO.updateSendStatus(mailObject.getId());
                        mailService.sendEmail(mailObject.getEmails(), mailObject.getBusinessName());
                        count++;
                        results.setValue(count + " sent OK to " + mailObject.getBusinessName());
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
        }
    }
}