package org.example.Services;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.Models.FoodOrder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class MongoDBService {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDBService() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("FoodOrder");
        collection = database.getCollection("Orders");
    }

    private Document orderToDocument(FoodOrder foodOrder) {

        return new Document("name", foodOrder.getClient())
                .append("tableNumber", foodOrder.getTableNumber())
                .append("dishes",foodOrder.getDishes())
                .append("status",foodOrder.getStatus())
                .append("orderTime",foodOrder.getOrderTime() == null ? null : foodOrder.getOrderTime().toString())
                .append("orderEndTime",foodOrder.getOrderEndTime() == null ? null : foodOrder.getOrderEndTime().toString());
    }

    public void addOrder(FoodOrder foodOrder) {
        Document doc = orderToDocument(foodOrder);
        collection.insertOne(doc);
        foodOrder.setId(doc.getObjectId("_id").toString());
    }

    private FoodOrder documentToFoodOrder(Document doc) {
        FoodOrder foodOrder = new FoodOrder();
        foodOrder.setId(doc.getObjectId("_id").toString());
        foodOrder.setClient(doc.getString("name"));
        foodOrder.setTableNumber(doc.getInteger("tableNumber"));
        foodOrder.setDishes(doc.getList("dishes",String.class));
        foodOrder.setStatus(doc.getString("status"));
        foodOrder.setOrderTime(formatDateTime(doc.getString("orderTime")));
        foodOrder.setOrderEndTime(formatDateTime(doc.getString("orderEndTime")));
        return foodOrder;
    }

    private LocalDateTime formatDateTime(String dateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime rentalDate;
        try {
            rentalDate = LocalDateTime.parse(dateTime, dateTimeFormatter);
        }catch(DateTimeParseException | NullPointerException e) {
            return null;
        }
        return rentalDate;
    }

    public FoodOrder getOrderById(String id) {
        Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return doc != null ? documentToFoodOrder(doc) : null;
    }

    public List<FoodOrder> getAllOrders() {
        List<FoodOrder> foodOrderArrayList = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                foodOrderArrayList.add(documentToFoodOrder(doc));
            }
        } finally {
            cursor.close();
        }
        return foodOrderArrayList;
    }

    public List<FoodOrder> getOrderByStatus(String status) {
        List<FoodOrder> foodOrderArrayList = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find(Filters.eq("status", status)).iterator();
        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                foodOrderArrayList.add(documentToFoodOrder(doc));
            }
        } finally {
            cursor.close();
        }
        return foodOrderArrayList;
    }

    public void updateOrder(String id, FoodOrder foodOrder) {
        Document updatedDoc = orderToDocument(foodOrder);
        collection.updateOne(Filters.eq("_id", new ObjectId(id)), new Document("$set", updatedDoc));
    }

    public void deleteTickedByID(String id) {
        collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
        System.out.println("Deleted order with id: " + id);
    }

    public void close() {
        mongoClient.close();
    }


}

