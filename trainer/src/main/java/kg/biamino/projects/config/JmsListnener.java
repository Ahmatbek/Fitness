package kg.biamino.projects.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;

@Configuration
public class JmsListnener {

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter jacksonJmsMessageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setMessageConverter(jacksonJmsMessageConverter);
        factory.setConnectionFactory(connectionFactory);
        factory.setSessionTransacted(true);
        return factory;
    }
}
