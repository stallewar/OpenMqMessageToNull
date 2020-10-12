package org.tqa.opemmqmessagetonull;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class QueueConsumer implements MessageListener {

  @SneakyThrows
  @Override
  public void onMessage(Message message) {
    ObjectMessage objMsg = (ObjectMessage) message;
    log.info("Message received : " + "JmsMessageID = " + message.getJMSMessageID()
        + ", ApplicationName = " + objMsg.getObject().toString());
  }
}
