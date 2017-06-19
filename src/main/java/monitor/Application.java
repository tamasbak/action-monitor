package monitor;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

import javax.jms.Queue;
import javax.sql.DataSource;

import org.apache.activemq.command.ActiveMQQueue;
import org.hsqldb.util.DatabaseManagerSwing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jms.annotation.EnableJms;

import static monitor.util.ApplicationConstants.MQ_NAME;

@SpringBootApplication
@EnableJms
public class Application {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	 public static void main(String[] args) throws SQLException {

		SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
		builder.headless(false).run(args);

		LOGGER.info("Starting DB manager.");
		DatabaseManagerSwing.main(new String[] { "--url", "jdbc:hsqldb:mem:testdb", "--user", "sa", "--password", "" });
	}

	@Bean
	public DataSource dataSource() {
		LOGGER.info("Creating embedded database.");
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
	}

	@Bean
	public Queue queue() {
		LOGGER.info("Creating MQ with name: " + MQ_NAME + ".");
		return new ActiveMQQueue(MQ_NAME);
	}
}
