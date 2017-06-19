package monitor.trigger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.hsqldb.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static monitor.util.ApplicationConstants.MQ_NAME;

@Component
public class PersonTrigger implements Trigger {

	private static final String TRIGGER_IMPLEMENTATION = PersonTrigger.class.getName();
	private static final String TABLE_NAME = "PERSON";

	@Autowired
	private DataSource dataSource;

	@PostConstruct
	public void init() throws SQLException {

		Connection conn = dataSource.getConnection();
		Statement stmt = conn.createStatement();

		stmt.execute(getTriggerDDL("tia_", "INSERT"));
		stmt.execute(getTriggerDDL("tua_", "UPDATE"));
		stmt.execute(getTriggerDDL("tda_", "DELETE"));

		stmt.close();
		conn.close();
	}
	

	/**
	 * A sample HSQLDB Trigger interface implementation.
	 * <p>
	 *
	 * This sample prints information about the firing trigger and records
	 * actions in an audit table.
	 * <p>
	 *
	 * The techniques used here are simplified dramatically for demonstration
	 * purposes and are in no way recommended as a model upon which to build
	 * actual installations involving triggered actions.
	 *
	 * @param triggerType
	 *            trigger type
	 * @param triggerName
	 *            trigger name
	 * @param tableName
	 *            table name
	 * @param oldRows
	 *            old row
	 * @param newRows
	 *            new row
	 */
	@Override
	public void fire(int triggerType, String triggerName, String tableName, Object[] oldRows, Object[] newRows) {

		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

			ActiveMQConnection connectionn = (ActiveMQConnection) connectionFactory.createConnection();
			connectionn.start();

			Session session = connectionn.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Destination destination = session.createQueue(MQ_NAME);

			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			StringBuilder messageBuilder = new StringBuilder("");
			
			switch (triggerType) {

			case INSERT_AFTER:
				messageBuilder.append("New Person entry is inserted.");
				break;
			case DELETE_AFTER:
				messageBuilder.append("Person entry is deleted.");
				break;
			case UPDATE_AFTER:
				messageBuilder.append("Person entry is updated.");
				break;

			default:
				break;
			}
			
			TextMessage message = session.createTextMessage(messageBuilder.toString());

			producer.send(message);

			session.close();
			connectionn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getTriggerDDL(String triggerPrefix, String operation) throws SQLException {

		StringBuilder sb = new StringBuilder();

		sb.append("CREATE TRIGGER ");
		sb.append(triggerPrefix);
		sb.append(TABLE_NAME);
		sb.append(' ');
		sb.append("AFTER");
		sb.append(' ');
		sb.append(operation);
		sb.append(" ON ");
		sb.append(TABLE_NAME);
		sb.append(' ');
		sb.append("QUEUE 0");
		sb.append(" CALL \"");
		sb.append(TRIGGER_IMPLEMENTATION);
		sb.append("\"");

		return sb.toString();
	}

}
