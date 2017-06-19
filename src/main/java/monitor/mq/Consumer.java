package monitor.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import monitor.message.Message;
import static monitor.util.ApplicationConstants.MQ_NAME;

@Component
public class Consumer {

	@Autowired
	private SimpMessagingTemplate template;

	@JmsListener(destination = MQ_NAME)
	public void receiveQueue(String text) {
		template.convertAndSend("/topic/action-monitor", new Message(text));
	}

}