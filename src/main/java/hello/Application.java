package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Creating tables");
        jdbcTemplate.execute("DROP TABLE Customer IF Exists");
        jdbcTemplate.execute("CREATE TABLE Customer(ID SERIAL, FirstName VARCHAR(255), LastName VARCHAR(255))");
        List<Object[]> splitupNames = Arrays.asList("Jackie Chan", "Arnold Shwarz", "John Woo", "Josh Dean").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());
        splitupNames.forEach(name -> log.info("Customer: FirstName: " + name[0] + " LastName: " + name[1]));
        jdbcTemplate.batchUpdate("INSERT INTO Customer(FirstName, LastName) VALUES(?, ?)", splitupNames);
        log.info("Querying all customer records");
        jdbcTemplate.query("SELECT ID, FirstName, LastName From Customer",
                (rs, rowNum) -> new Customer(rs.getLong("ID"), rs.getString("FirstName"), rs.getString("LastName")))
                .forEach( customer -> log.info("Customer is : " + customer.toString()));
    }
}
