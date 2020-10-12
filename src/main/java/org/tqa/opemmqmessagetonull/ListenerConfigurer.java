package org.tqa.opemmqmessagetonull;

import static com.sun.messaging.ConnectionConfiguration.imqAddressList;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.QueueConnectionFactory;
import java.util.UUID;
import javax.jms.JMSException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Configuration
@EnableConfigurationProperties
@EnableJms
@Slf4j
public class ListenerConfigurer implements JmsListenerConfigurer {

  @Autowired
  QueueConsumer queueConsumer;

  @Value("${mq.addresses}")
  private String mqAddresses;

  @Value("${mq.queue_name}")
  private String queueName;

  @SneakyThrows
  @Override
  public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
    String[] mqAddress = mqAddresses.split(",");
    log.info("ImqAddressesList = " + mqAddresses);
    for (String s : mqAddress) {
      try {
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setId(String.valueOf(UUID.randomUUID()));
        endpoint.setDestination(queueName);
        endpoint.setMessageListener(queueConsumer);
        endpoint.setConcurrency("1-4");

        QueueConnectionFactory queueConnectionFactory = new QueueConnectionFactory();
        queueConnectionFactory.setProperty(imqAddressList, s);
        queueConnectionFactory.setProperty(ConnectionConfiguration.imqReconnectEnabled, "true");
        queueConnectionFactory.setProperty(
            ConnectionConfiguration.imqConfiguredClientID, UUID.randomUUID().toString());

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(queueConnectionFactory);

        DefaultMessageListenerContainer container = factory.createListenerContainer(endpoint);
        endpoint.setupListenerContainer(container);

        registrar.registerEndpoint(endpoint, factory);
        registrar.setContainerFactory(factory);

        log.info("Connecting to  = " + s);
      } catch (JMSException e) {
        e.printStackTrace();
      }
    }
  }
}
