package kg.biamino.projects.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class ActiveMQConfig {
    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;
    @Value("${spring.activemq.user}")
    private String user;
    @Value("${spring.activemq.password}")
    private String password;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connectionFactory.setUserName(user);
        connectionFactory.setPassword(password);

        RedeliveryPolicy redeliveryPolicy = connectionFactory.getRedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(1000);
        redeliveryPolicy.setRedeliveryDelay(5000);
        redeliveryPolicy.setMaximumRedeliveries(3);

        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        return connectionFactory;
    }
    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter(objectMapper.registerModule(new JavaTimeModule()));
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
