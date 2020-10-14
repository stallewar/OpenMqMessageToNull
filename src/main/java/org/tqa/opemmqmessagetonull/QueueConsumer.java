package org.tqa.opemmqmessagetonull;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class QueueConsumer implements MessageListener {

  @SneakyThrows
  @Override
  public void onMessage(Message message) {
    if (message instanceof TextMessage) {
      log.info("Message received : " + "JmsMessageID = " + message.getJMSMessageID()
          + ((TextMessage) message).getText());
    }
    if (message instanceof ObjectMessage) {
      log.info("Message received : " + "JmsMessageID = " + message.getJMSMessageID() + message);
    } else {
      throw new IllegalArgumentException("Unknown message type " + message.getJMSType());
    }
  }
}
