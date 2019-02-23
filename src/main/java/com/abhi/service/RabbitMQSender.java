package com.abhi.service;

import java.util.UUID;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.abhi.model.Employee;

@Service
public class RabbitMQSender {
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Value("${rabbitmq.queue}")
	private String queue;
	
	public void send(Employee company) {
		company.setId(UUID.randomUUID().toString());
		rabbitTemplate.convertAndSend(queue,company);
		System.out.println("Send msg = " + company);
	    
	}
}