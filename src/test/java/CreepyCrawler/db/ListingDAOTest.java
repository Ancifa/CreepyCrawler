package CreepyCrawler.db;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Component
public class ListingDAOTest {

    private DataSource dataSource;

    public ListingDAOTest() {
        this.dataSource = DataSourceBuilder
                .create()
                .username("qxdzjhmn_ancifa")
                .password("ancifa")
                .url("jdbc:mysql://pandora.lite-host.in:3306/qxdzjhmn_creepycrawler")
                .driverClassName("com.mysql.jdbc.Driver")
                .build();
/*        dataSource = DataSourceBuilder
                .create()
                .username("Ancifa")
                .password("xfosus")
                .url("jdbc:mysql://localhost:3306/crawler")
                .driverClassName("com.mysql.jdbc.Driver")
                .build();*/
    }

    private List<String> findAllBookings() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select email from email where id in (1,2)",
                (rs, rowNum) -> rs.getString("email"));
    }

    @Ignore
    @Test
    public void testDb() throws SQLException, ClassNotFoundException {
        ListingDAO listingDAO = new ListingDAO();
        listingDAO.getListingsAndEmailsAmount();

/*        List<String> res = findAllBookings();
        System.out.println(res.get(0));*/
    }
}