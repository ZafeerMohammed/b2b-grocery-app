package com.b2bapp.grocery.service;

import com.b2bapp.grocery.dto.ProductSalesStatsDTO;
import com.b2bapp.grocery.model.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    Order checkout(String retailerEmail);

    List<Order> getOrders(String retailerEmail);

    List<Order> getAllOrders();

    List<Order> getOrdersByWholesalerEmail(String wholesalerEmail);

    List<ProductSalesStatsDTO> getWholesalerProductStats(String email, String category, LocalDate startDate, LocalDate endDate);

    List<ProductSalesStatsDTO> getTopSellingProducts(String email, String category, LocalDate startDate, LocalDate endDate);


}
