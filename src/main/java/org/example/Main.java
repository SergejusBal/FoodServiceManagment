package org.example;


import org.example.Services.OverDueOrDerTracker;
import org.example.Services.RegisterOrder;
import org.example.Services.SetPaymentStatus;
import org.example.Services.UpdateOrderStatus;

public class Main {
    public static void main(String[] args) {

        RegisterOrder registerOrder = new RegisterOrder("FoodOrder");
        Thread registerOrderThread = new Thread(registerOrder);
        registerOrderThread.start();

        UpdateOrderStatus updateOrder = new UpdateOrderStatus("FoodOrderUpdate");
        Thread updateOrderThread = new Thread(updateOrder);
        updateOrderThread.start();

        SetPaymentStatus setPaymentStatus = new SetPaymentStatus("PaymentUpdate");
        Thread setPaymentStatusUpdate = new Thread(setPaymentStatus);
        setPaymentStatusUpdate.start();

        OverDueOrDerTracker overDueOrDerTracker = new OverDueOrDerTracker();
        Thread overDueOrDerTrackerThread = new Thread(overDueOrDerTracker);
        overDueOrDerTrackerThread.start();

    }
}