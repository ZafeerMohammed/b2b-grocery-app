package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.TopRetailerForWholesalerDTO;
import com.b2bapp.grocery.dto.TotalSalesStatsDTO;
import com.b2bapp.grocery.dto.RecentOrderDTO;
import com.b2bapp.grocery.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/wholesaler/dashboard")
@RequiredArgsConstructor
public class WholesalerDashboardController {

    private final OrderService orderService;

    @GetMapping("/sales")
    public ResponseEntity<TotalSalesStatsDTO> getSalesStats(
            Principal principal,
            @RequestParam String period // today, week, month
    ) {
        String email = principal.getName();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start, end;

        switch (period.toLowerCase()) {
            case "today" -> {
                start = now.toLocalDate().atStartOfDay();
                end = now.toLocalDate().atTime(23, 59, 59);
            }
            case "week" -> {
                start = now.toLocalDate().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
                end = start.toLocalDate().plusDays(6).atTime(23, 59, 59);
            }
            case "month" -> {
                start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                end = start.toLocalDate().plusMonths(1).minusDays(1).atTime(23, 59, 59);
            }
            default -> throw new IllegalArgumentException("Invalid period. Use today, week or month");
        }

        return ResponseEntity.ok(orderService.getTotalSalesStatsForWholesaler(email, start, end));
    }

    @GetMapping("/top-retailers")
    public ResponseEntity<List<TopRetailerForWholesalerDTO>> getTopRetailers(
            Principal principal,
            @RequestParam String period
    ) {
        String email = principal.getName();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start, end;

        switch (period.toLowerCase()) {
            case "today" -> {
                start = now.toLocalDate().atStartOfDay();
                end = now.toLocalDate().atTime(23, 59, 59);
            }
            case "week" -> {
                start = now.toLocalDate().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
                end = start.toLocalDate().plusDays(6).atTime(23, 59, 59);
            }
            case "month" -> {
                start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                end = start.toLocalDate().plusMonths(1).minusDays(1).atTime(23, 59, 59);
            }
            default -> throw new IllegalArgumentException("Invalid period.");
        }

        return ResponseEntity.ok(orderService.getTopRetailersForWholesaler(email, start, end));
    }

    @GetMapping("/recent-orders")
    public ResponseEntity<List<RecentOrderDTO>> getRecentOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getRecentOrdersForWholesaler(principal.getName()));
    }
}
