package com.b2bapp.grocery.service;

import com.b2bapp.grocery.dto.*;
import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.model.OrderStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderService {

    Order checkout(String retailerEmail);

    // Retailer-specific
    Page<Order> getOrders(String retailerEmail, int page, int size);
    Page<Order> filterRetailerOrders(String email, String category, LocalDate start, LocalDate end, int page, int size);
    boolean cancelOrderIfPossible(UUID orderId, String retailerEmail);


    // Wholesaler-specific
    Page<Order> getOrdersByWholesalerEmail(String wholesalerEmail, int page, int size);
    Page<Order> filterWholesalerOrders(String email, String category, LocalDate start, LocalDate end, int page, int size);
    TotalSalesStatsDTO getTotalSalesStatsForWholesaler(String wholesalerEmail, LocalDateTime start, LocalDateTime end);
    List<TopRetailerForWholesalerDTO> getTopRetailersForWholesaler(String wholesalerEmail, LocalDateTime start, LocalDateTime end);
    List<RecentOrderDTO> getRecentOrdersForWholesaler(String wholesalerEmail);
    void updateOrderStatus(UUID orderId, OrderStatus newStatus, String wholesalerEmail);
    Map<String, Double> getWholesalerCategoryWiseSales(String email, LocalDate startDate, LocalDate endDate);
    List<ProductSalesStatsDTO> getTopProductsForWholesaler(String wholesalerEmail, LocalDate start, LocalDate end);


    // Admin
    Page<Order> filterOrdersForAdmin(String retailerEmail, String wholesalerEmail, String category, LocalDate start, LocalDate end, int page, int size);
    Page<Order> getAllOrders(int page, int size); // if needed for admin raw view
    List<RecentOrderDTO> getRecentOrders();
    Map<String, Double> getCategoryWiseSales(LocalDateTime start, LocalDateTime end);
    List<ProductSalesStatsDTO> getTopSellingProductsForAdmin(LocalDateTime start, LocalDateTime end);

    // Stats
    List<ProductSalesStatsDTO> getWholesalerProductStats(String email, String category, LocalDate startDate, LocalDate endDate);
    List<ProductSalesStatsDTO> getTopSellingProducts(String email, String category, LocalDate startDate, LocalDate endDate);
    TotalSalesStatsDTO getTotalSalesStatsForPeriod(LocalDateTime start, LocalDateTime end);
    List<TopWholesalerDTO> getTop5Wholesalers(LocalDateTime start, LocalDateTime end);
    List<TopCategoryDTO> getTop5Categories(LocalDateTime start, LocalDateTime end);
    List<MonthlySalesDTO> getMonthlySalesOverview(int year);
    List<TopRetailerDTO> getTopRetailers(LocalDateTime start, LocalDateTime end);




}



