package CreepyCrawler.mail;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * Created by i on 11.03.2018.
 */
public class MailService {

    public static final String YAHOO_ADDRESS = "ancifa@yahoo.com";
    public static final String GMAIL_ADDRESS = "ancifa70@gmail.com";

    private String mailProvider;
    private String password;
    private String bcc;

    private Properties properties;

    public MailService(String mailProvider, String password) {
        this.mailProvider = mailProvider;
        this.password = password;

        properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        if (YAHOO_ADDRESS.equals(mailProvider)) {
            properties.put("mail.smtp.host", "smtp.mail.yahoo.com");
            bcc = YAHOO_ADDRESS;
        } else if (GMAIL_ADDRESS.equals(mailProvider)) {
            properties.put("mail.smtp.host", "smtp.gmail.com");
            bcc = "";
        }
//        properties.put("mail.debug", "true");
    }

    public void sendEmail(String sendToEmailAddress, String businessName) throws MessagingException {
        Session session = Session.getInstance(properties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailProvider, password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mailProvider));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendToEmailAddress));
        message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
        message.setSubject("Support request to " + businessName);
        message.setText(getLetterText(businessName));

        Transport.send(message);
    }

    private String getLetterText(String businessName) {
        StringBuilder text = new StringBuilder();
        text.append("Dear Sirs at the ")
                .append(businessName)
                .append(",\n\n")
                .append("We are a team of enthusiasts, who help seniors, disabled people and people " +
                        "with very low income in Moscow, Russia to get access to information technologies " +
                        "which they are not able to get by themselves.\n" +
                        "We provide such people with used computers, lap-tops, monitors, printers, modems, " +
                        "routers and other equipment as well as with some technical support. " +
                        "We do it with no cost for them as we are not-for-profit organization.\n\n")
                .append("If you support our ideas and are willing to help seniors, disabled people and people " +
                        "with very low income of Russia to get access to information technologies we would " +
                        "appreciate any amount of your donation to our PayPal account " +
                        "http://www.paypal.me/compdonor/.\n\n")
                .append("Please visit us on http://comp-donor.mywebcommunity.org/start_eng.html.\n\n")
                .append("Thanks a lot and God Bless You!\n\n")
                .append("Sincerely,\n\nDimitri Arkhipov\nComputer Donor");

        return text.toString();
    }
}
