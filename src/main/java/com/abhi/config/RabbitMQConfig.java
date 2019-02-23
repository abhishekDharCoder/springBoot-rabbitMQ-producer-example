package com.abhi.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Abhishek
 *
 */
@Configuration
@EnableRabbit
public class RabbitMQConfig {

	@Value("${rabbitmq.queue}")
	String queueName;

	@Value("${rabbitmq.exchange}")
	String exchange;

	@Value("${rabbitmq.dlqueue}")
	String dlQueueName;

	@Value("${rabbitmq.dlexchange}")
	String dlExchange;

	@Value("${rabbitmq.retry}")
	String retry;

	/**
	 * 
	 * This bean declaration is mandatory for declaring queues if they are not exist
	 * on MQ server
	 * 
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}

	@Bean
	Queue queue() {
		return QueueBuilder.durable(queueName).withArgument("x-max-priority", 10)
				.withArgument("x-dead-letter-exchange", dlExchange)
				.withArgument("x-dead-letter-routing-key", dlQueueName).build();
	}

	@Bean
	DirectExchange exchange() {
		return new DirectExchange(exchange);
	}

	@Bean
	Binding binding(Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(queue.getName());
	}

	/**
	 * Setting up dead letter queue 
	 * in case the message is corrupted or listener
	 * throws any exception
	 * @return
	 */
	@Bean
	Queue dlQueue() {
		return QueueBuilder.durable(dlQueueName).withArgument("x-dead-letter-exchange", exchange)
				.withArgument("x-dead-letter-routing-key", queueName)
				.withArgument("x-message-ttl", Long.parseLong(retry)).build();
	}

	@Bean
	DirectExchange dlExchange() {
		return new DirectExchange(dlExchange);
	}

	@Bean
	Binding dlBinding(Queue dlQueue, DirectExchange dlExchange) {
		return BindingBuilder.bind(dlQueue).to(dlExchange).with(dlQueue.getName());
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setExchange(exchange);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}
}
