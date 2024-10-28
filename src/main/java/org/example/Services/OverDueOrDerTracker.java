package org.example.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.Models.FoodOrder;
import org.example.UtilityServices.MongoDBService;
import org.example.UtilityServices.RedisService;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class OverDueOrDerTracker implements Runnable{

    private final MongoDBService mongoDBService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    public OverDueOrDerTracker() {
        mongoDBService = new MongoDBService();
        redisService = new RedisService("localhost", 6379);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void run() {
        while(true) {
            checkAndModifyStatusToOverdue();
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void checkAndModifyStatusToOverdue(){
    //    List<FoodOrder> foodOrderList = mongoDBService.getOrderByStatus("pending");
        List<FoodOrder> foodOrderList = mongoDBService.getOrderWithEmptyOrderEndTime();

        for(FoodOrder foodOrder: foodOrderList){
            Duration duration = Duration.between(foodOrder.getOrderTime(), LocalDateTime.now());
            if(duration.toMinutes() > 30){
                foodOrder.setStatus("Overdue");
                foodOrder.setOrderEndTime(LocalDateTime.now());
                modifyStatusToOverdue(foodOrder);
            }
        }
    }

    public void modifyStatusToOverdue(FoodOrder foodOrder){
        mongoDBService.updateOrder(foodOrder.getId(), foodOrder);
        try {
            redisService.put(foodOrder.getId(),generateJson(foodOrder),300);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
