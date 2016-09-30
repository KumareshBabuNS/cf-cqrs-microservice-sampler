package io.benwilcock.productquery.configuration;

import org.axonframework.contextsupport.spring.AnnotationDriven;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ben on 19/02/16.
 */
@Configuration
@AnnotationDriven
public class RabbitConfiguration {

    @Value("${spring.application.exchange}")
    private String exchangeName;

    @Value("${spring.application.queue}")
    private String queueName;

    @Bean
    public String uniqueQueueName() {
        //return queueName + "." + index;
        return queueName;
    }

    @Bean
    Queue eventStream(String uniqueQueueName) {
        return new Queue(uniqueQueueName, false, false, true);
    }

    @Bean
    FanoutExchange eventBusExchange() {
        return new FanoutExchange(exchangeName, true, false);
    }

    @Bean
    Binding binding(String uniqueQueueName) {
        return new Binding(uniqueQueueName, Binding.DestinationType.QUEUE, exchangeName, "*.*", null);
    }

    @Bean
    @Required
    RabbitAdmin rabbitAdmin(String uniqueQueueName, ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        admin.declareExchange(eventBusExchange());
        admin.declareQueue(eventStream(uniqueQueueName));
        admin.declareBinding(binding(uniqueQueueName));
        return admin;
    }
}
