package org.example;


import org.example.Services.OverDueOrDerTracker;
import org.example.Services.RegisterOrder;
import org.example.Services.UpdateOrder;

public class Main {
    public static void main(String[] args) {

        RegisterOrder registerOrder = new RegisterOrder("FoodOrder");
        Thread registerOrderThread = new Thread(registerOrder);
        registerOrderThread.start();

        UpdateOrder updateOrder = new UpdateOrder("FoodOrderUpdate");
        Thread updateOrderThread = new Thread(updateOrder);
        updateOrderThread.start();

        OverDueOrDerTracker overDueOrDerTracker = new OverDueOrDerTracker();
        Thread overDueOrDerTrackerThread = new Thread(overDueOrDerTracker);
        overDueOrDerTrackerThread.start();




    }
}