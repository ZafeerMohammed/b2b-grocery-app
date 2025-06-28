package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.*;
import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.service.AdminService;
import com.b2bapp.grocery.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final OrderService orderService;




    // Dashboard Stats
    @GetMapping("/sales")
    public ResponseEntity<TotalSalesStatsDTO> getSalesStats(
            @RequestParam String period // "today", "week", or "month"
    ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start, end;

        switch (period.toLowerCase()) {
            case "today" -> {
                start = now.toLocalDate().atStartOfDay();
                end = now.toLocalDate().atTime(23, 59, 59, 999999999);
            }
            case "week" -> {
                start = now.toLocalDate().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
                end = start.plusDays(6).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            }
            case "month" -> {
                start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                end = start.plusMonths(1).minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            }
            default -> throw new IllegalArgumentException("Invalid period. Use 'today', 'week', or 'month'");
        }

        return ResponseEntity.ok(orderService.getTotalSalesStatsForPeriod(start, end));
    }




    @GetMapping("/top-wholesalers")
    public ResponseEntity<List<TopWholesalerDTO>> getTopWholesalers(
            @RequestParam String period // "today", "week", or "month"
    ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start, end;

        switch (period.toLowerCase()) {
            case "today" -> {
                start = now.toLocalDate().atStartOfDay();
                end = now.toLocalDate().atTime(23, 59, 59, 999999999);
            }
            case "week" -> {
                start = now.toLocalDate().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
                end = start.plusDays(6).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            }
            case "month" -> {
                start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                end = start.plusMonths(1).minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            }
            default -> throw new IllegalArgumentException("Invalid period. Use 'today', 'week', or 'month'");
        }

        return ResponseEntity.ok(orderService.getTop5Wholesalers(start, end));
    }


    @GetMapping("/top-categories")
    public ResponseEntity<List<TopCategoryDTO>> getTopCategories(
            @RequestParam String period // "today", "week", or "month"
    ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start, end;

        switch (period.toLowerCase()) {
            case "today" -> {
                start = now.toLocalDate().atStartOfDay();
                end = now.toLocalDate().atTime(23, 59, 59, 999999999);
            }
            case "week" -> {
                start = now.toLocalDate().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
                end = start.plusDays(6).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            }
            case "month" -> {
                start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                end = start.plusMonths(1).minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            }
            default -> throw new IllegalArgumentException("Invalid period. Use 'today', 'week', or 'month'");
        }

        return ResponseEntity.ok(orderService.getTop5Categories(start, end));
    }

    @GetMapping("/monthly-sales")
    public ResponseEntity<List<MonthlySalesDTO>> getMonthlySales(@RequestParam int year) {
        return ResponseEntity.ok(orderService.getMonthlySalesOverview(year));
    }


    @GetMapping("/top-retailers")
    public ResponseEntity<List<TopRetailerDTO>> getTopRetailers(@RequestParam String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start, end;

        switch (period.toLowerCase()) {
            case "today" -> {
                start = now.toLocalDate().atStartOfDay();
                end = now.toLocalDate().atTime(23, 59, 59, 999999999);
            }
            case "week" -> {
                start = now.toLocalDate().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
                end = start.plusDays(6).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            }
            case "month" -> {
                start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                end = start.plusMonths(1).minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            }
            default -> throw new IllegalArgumentException("Invalid period. Use 'today', 'week', or 'month'");
        }

        return ResponseEntity.ok(orderService.getTopRetailers(start, end));
    }


    @GetMapping("/recent-orders")
    public ResponseEntity<List<RecentOrderDTO>> getRecentOrders() {
        return ResponseEntity.ok(orderService.getRecentOrders());
    }

}
