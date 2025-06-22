package com.b2bapp.grocery.service;

import com.b2bapp.grocery.dto.ProductSalesStatsDTO;
import com.b2bapp.grocery.dto.TopCategoryDTO;
import com.b2bapp.grocery.dto.TopWholesalerDTO;
import com.b2bapp.grocery.dto.TotalSalesStatsDTO;
import com.b2bapp.grocery.model.Order;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    Order checkout(String retailerEmail);

    // Retailer-specific
    Page<Order> getOrders(String retailerEmail, int page, int size);
    Page<Order> filterRetailerOrders(String email, String category, LocalDate start, LocalDate end, int page, int size);

    // Wholesaler-specific
    Page<Order> getOrdersByWholesalerEmail(String wholesalerEmail, int page, int size);
    Page<Order> filterWholesalerOrders(String email, String category, LocalDate start, LocalDate end, int page, int size);

    // Admin
    Page<Order> filterOrdersForAdmin(String retailerEmail, String wholesalerEmail, String category, LocalDate start, LocalDate end, int page, int size);
    Page<Order> getAllOrders(int page, int size); // if needed for admin raw view

    // Stats
    List<ProductSalesStatsDTO> getWholesalerProductStats(String email, String category, LocalDate startDate, LocalDate endDate);
    List<ProductSalesStatsDTO> getTopSellingProducts(String email, String category, LocalDate startDate, LocalDate endDate);
    TotalSalesStatsDTO getTotalSalesStatsForPeriod(LocalDateTime start, LocalDateTime end);
    List<TopWholesalerDTO> getTop5Wholesalers(LocalDateTime start, LocalDateTime end);
    List<TopCategoryDTO> getTop5Categories(LocalDateTime start, LocalDateTime end);


}



