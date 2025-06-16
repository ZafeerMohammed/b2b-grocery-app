package com.b2bapp.grocery.service;

import com.b2bapp.grocery.model.Order;

import java.util.List;

public interface OrderService {
    Order checkout(String retailerEmail);
    List<Order> getOrders(String retailerEmail);
}
