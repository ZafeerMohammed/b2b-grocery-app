package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.*;
import com.b2bapp.grocery.service.OrderService;
import com.b2bapp.grocery.service.ProductService;
import com.b2bapp.grocery.service.ReturnRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wholesaler/dashboard")
@RequiredArgsConstructor
public class WholesalerDashboardController {

    private final OrderService orderService;
    private final ProductService productService;
    private final ReturnRequestService returnService;

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


    @GetMapping("/orders/stats")
    public ResponseEntity<List<ProductSalesStatsDTO>> getProductSalesStats(
            Principal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(orderService.getWholesalerProductStats(principal.getName(), category, startDate, endDate));
    }


    @GetMapping("/orders/top-selling")
    public ResponseEntity<List<ProductSalesStatsDTO>> getTopSellingProducts(
            Principal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(orderService.getTopSellingProducts(principal.getName(), category, startDate, endDate));
    }


    @GetMapping("/product-stats")
    public ResponseEntity<List<ProductSalesStatsDTO>> getProductStats(
            Principal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "false") boolean topOnly
    ) {
        String email = principal.getName();

        List<ProductSalesStatsDTO> result = topOnly
                ? orderService.getTopSellingProducts(email, category, startDate, endDate)
                : orderService.getWholesalerProductStats(email, category, startDate, endDate);

        return ResponseEntity.ok(result);
    }


    @GetMapping("/category-sales")
    public ResponseEntity<Map<String, Double>> getSalesByCategory(
            Principal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        String email = principal.getName();
        Map<String, Double> stats = orderService.getWholesalerCategoryWiseSales(email, startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/category-sales")
    public ResponseEntity<Map<String, Double>> getCategorySales(
            Principal principal,
            @RequestParam String period
    ) {
        String email = principal.getName();
        LocalDateTime now = LocalDateTime.now();
        LocalDate start, end;

        switch (period.toLowerCase()) {
            case "today" -> {
                start = now.toLocalDate();
                end = now.toLocalDate();
            }
            case "week" -> {
                start = now.toLocalDate().with(java.time.DayOfWeek.MONDAY);
                end = start.plusDays(6);
            }
            case "month" -> {
                start = now.withDayOfMonth(1).toLocalDate();
                end = start.plusMonths(1).minusDays(1);
            }
            default -> throw new IllegalArgumentException("Invalid period");
        }

        return ResponseEntity.ok(orderService.getWholesalerCategoryWiseSales(email, start, end));
    }




    @GetMapping("/recent-orders")
    public ResponseEntity<List<RecentOrderDTO>> getRecentOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getRecentOrdersForWholesaler(principal.getName()));
    }

    @GetMapping("/low-stock-products")
    public ResponseEntity<List<LowStockProductDTO>> getLowStockProducts(Principal principal) {
        return ResponseEntity.ok(productService.getLowStockProductsForWholesaler(principal.getName()));
    }

    @GetMapping("/returns")
    public ResponseEntity<ReturnStatsDTO> getReturnStats(
            Principal principal,
            @RequestParam String period // today, week, month
    ) {
        LocalDate now = LocalDate.now();
        LocalDate start, end;

        switch (period.toLowerCase()) {
            case "today" -> {
                start = now;
                end = now;
            }
            case "week" -> {
                start = now.with(java.time.DayOfWeek.MONDAY);
                end = start.plusDays(6);
            }
            case "month" -> {
                start = now.withDayOfMonth(1);
                end = start.plusMonths(1).minusDays(1);
            }
            default -> throw new IllegalArgumentException("Invalid period. Use today/week/month");
        }

        ReturnStatsDTO stats = returnService.getReturnStatsForWholesaler(principal.getName(), start, end);
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/top-returned-products")
    public ResponseEntity<List<TopReturnedProductDTO>> getTopReturnedProducts(
            Principal principal,
            @RequestParam String period
    ) {
        LocalDate now = LocalDate.now();
        LocalDate start, end;

        switch (period.toLowerCase()) {
            case "today" -> {
                start = now;
                end = now;
            }
            case "week" -> {
                start = now.with(java.time.DayOfWeek.MONDAY);
                end = start.plusDays(6);
            }
            case "month" -> {
                start = now.withDayOfMonth(1);
                end = start.plusMonths(1).minusDays(1);
            }
            default -> throw new IllegalArgumentException("Invalid period. Use today/week/month");
        }

        return ResponseEntity.ok(
                returnService.getTopReturnedProductsForWholesaler(principal.getName(), start, end)
        );
    }



    @GetMapping("/top-products")
    public ResponseEntity<List<ProductSalesStatsDTO>> getTopProducts(
            Principal principal,
            @RequestParam String period
    ) {
        String email = principal.getName();
        LocalDateTime now = LocalDateTime.now();
        LocalDate start, end;

        switch (period.toLowerCase()) {
            case "today" -> {
                start = now.toLocalDate();
                end = now.toLocalDate();
            }
            case "week" -> {
                start = now.toLocalDate().with(java.time.DayOfWeek.MONDAY);
                end = start.plusDays(6);
            }
            case "month" -> {
                start = now.withDayOfMonth(1).toLocalDate();
                end = start.plusMonths(1).minusDays(1);
            }
            default -> throw new IllegalArgumentException("Invalid period");
        }

        return ResponseEntity.ok(orderService.getTopProductsForWholesaler(email, start, end));
    }



}
