package CreepyCrawler.db;

import CreepyCrawler.crawler.model.Result;
import CreepyCrawler.mail.MailObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by i on 10.03.2018.
 */
public class ListingDAO {
    private final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private final String DB_URL = "jdbc:mysql://localhost/crawler";
    private final String DB_USER = "Ancifa";
    private final String DB_PASSWORD = "xfosus";

    public ListingDAO() throws SQLException, ClassNotFoundException {
    }

    private Connection makeConnection() throws ClassNotFoundException, SQLException {
        Class.forName(MYSQL_DRIVER);
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public void saveListingToDb(Result result, String category) throws SQLException, ClassNotFoundException {
        Connection connection = makeConnection();

        if (isListingExists(connection, result.getListingId())) {
            connection.close();
            return;
        }

        String statementString =
                "INSERT INTO crawler.listing (yp_id, name, city, state, category, search_key) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, Integer.parseInt(result.getListingId()));
        statement.setString(2, result.getBusinessName());
        statement.setString(3, result.getCity());
        statement.setString(4, result.getState());
        statement.setString(5, result.getPrimaryCategory());
        statement.setString(6, category);

        statement.executeUpdate();

        ResultSet resultSet = statement.getGeneratedKeys();
        resultSet.next();

        saveEmailToDb(connection, result, resultSet.getInt(1));
    }

    private boolean isListingExists(Connection connection, String listingId) throws SQLException {
        String statementString =
                "SELECT COUNT(*) FROM crawler.listing WHERE yp_id = " + Integer.parseInt(listingId);
        PreparedStatement statement = connection.prepareStatement(statementString);
//        statement.setInt(1, Integer.parseInt(listingId));
        ResultSet resultSet = statement.executeQuery(statementString);
        resultSet.next();

        return resultSet.getInt(1) > 0;
    }

    private void saveEmailToDb(Connection connection, Result result, int listingId) throws SQLException, ClassNotFoundException {
        String statementString =
                "INSERT INTO crawler.email (listing_id, email) VALUES (?, ?)";

        ArrayList<String> emails = result.getEmailsList();
        for (String email : emails) {
            PreparedStatement statement = connection.prepareStatement(statementString);
            statement.setInt(1, listingId);
            statement.setString(2, email);

            statement.executeUpdate();
        }
        connection.close();
    }

    public void updateSendStatus(int listingId) throws SQLException, ClassNotFoundException {
        Connection connection = makeConnection();

        String statementString = "UPDATE email SET is_sent = ?, send_date = ? WHERE listing_id = " + listingId;
        PreparedStatement statement = connection.prepareStatement(statementString);
        statement.setInt(1, 1);
        statement.setDate(2, new Date(Calendar.getInstance().getTimeInMillis()));
        statement.executeUpdate();

        connection.close();
    }

    public MailObject getFirstNotSentEmail() throws SQLException, ClassNotFoundException {
        Connection connection = makeConnection();

        String statementString = "SELECT listing_id FROM email WHERE is_sent = 0 LIMIT 1";
        PreparedStatement statement = connection.prepareStatement(statementString);
        ResultSet resultSet = statement.executeQuery(statementString);
        resultSet.next();
        int id = resultSet.getInt(1);

        String emails = getEmailsById(connection, id);
        String businessName = getNameById(connection, id);

        MailObject mailObject = new MailObject();
        mailObject.setId(id);
        mailObject.setEmails(emails);
        mailObject.setBusinessName(businessName);

        connection.close();

        return mailObject;
    }

    private String getEmailsById(Connection connection, int id) throws SQLException {
        String statementString = "SELECT email FROM email WHERE listing_id = " + id;
        PreparedStatement statement = connection.prepareStatement(statementString);
        ResultSet resultSet = statement.executeQuery(statementString);

        StringBuilder emails = new StringBuilder();
        int comma = 0;
        while (resultSet.next()) {
            if (comma > 0) {
                emails.append(",");
            }
            emails.append(resultSet.getString(1));
            comma++;
        }

        return emails.toString();
    }

    private String getNameById(Connection connection, int id) throws SQLException {
        String statementString = "SELECT name FROM listing WHERE id = " + id;
        PreparedStatement statement = connection.prepareStatement(statementString);
        ResultSet resultSet = statement.executeQuery(statementString);
        resultSet.next();

        return resultSet.getString(1);
    }
}
