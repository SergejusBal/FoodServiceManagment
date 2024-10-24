package org.example.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.Models.FoodOrder;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class UpdateOrder implements Runnable{
    private String queueName;
    private static final String HOST = "localhost";
    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;
    private MongoDBService mongoDBService;
    private RedisService redisService;

    public UpdateOrder(String queueName) {
        this.factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.queueName = queueName;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mongoDBService = new MongoDBService();
        redisService = new RedisService("localhost", 6379);
    }


    @Override
    public void run() {
        openRabbitChannel();
    }

    private void openRabbitChannel() {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, false, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String jsonMessage = new String(delivery.getBody(), "UTF-8");
                try {

                    FoodOrder foodOrderUpdate = generateObjectFromJSon(jsonMessage,FoodOrder.class);
                    FoodOrder foodOrderFromDB = mongoDBService.getOrderById(foodOrderUpdate.getId());

                    foodOrderFromDB.setOrderEndTime(foodOrderUpdate.getOrderEndTime());
                    foodOrderFromDB.setStatus(foodOrderUpdate.getStatus());

                    mongoDBService.updateOrder(foodOrderUpdate.getId(), foodOrderFromDB);
                    redisService.put(foodOrderUpdate.getId(),generateJson(foodOrderFromDB),300);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

            while (true) {
                Thread.sleep(1000);
            }
        }catch (IOException | TimeoutException | InterruptedException  e){
            System.out.println(e.getMessage());
        }

    }

    private  <T> T generateObjectFromJSon(String json, Class<T> clazz){

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private <T> String generateJson(T t)   {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return "{}";
        }
    }

}