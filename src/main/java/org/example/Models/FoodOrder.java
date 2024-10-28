package org.example.Models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class FoodOrder {
    private String id;
    private String client;
    private int tableNumber;
    private List<String> dishes;
    private String status;
    @JsonProperty("orderTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;
    @JsonProperty("orderEndTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderEndTime;
    private Double paymentAmount;
    private String paymentMethod;

    public FoodOrder() {

    }

    public String getId() {
        return id;
    }

    public String getClient() {
        return client;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public List<String> getDishes() {
        return dishes;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public LocalDateTime getOrderEndTime() {
        return orderEndTime;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public void setDishes(List<String> dishes) {
        this.dishes = dishes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public void setOrderEndTime(LocalDateTime orderEndTime) {
        this.orderEndTime = orderEndTime;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
