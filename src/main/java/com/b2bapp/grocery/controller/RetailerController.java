package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.CartItemRequestDTO;
import com.b2bapp.grocery.model.CartItem;
import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.service.CartService;
import com.b2bapp.grocery.service.OrderService;
import com.b2bapp.grocery.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/retailer")
@RequiredArgsConstructor
public class RetailerController {

    private final CartService cartService;
    private final OrderService orderService;
    private final ProductService productService;

    // Add product to cart
    @PostMapping("/cart/add")
    public ResponseEntity<String> addToCart(@RequestBody CartItemRequestDTO request) {
        cartService.addToCart(request.getRetailerEmail(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok("Product added to cart");
    }

    // View all cart items for a retailer
    @GetMapping("/cart")
    public ResponseEntity<List<CartItem>> getCartItems(Principal principal) {
        return ResponseEntity.ok(cartService.getCartItems(principal.getName()));
    }

    // Remove specific item from cart
    @DeleteMapping("/cart/remove/{itemId}")
    public ResponseEntity<String> removeCartItem(@PathVariable UUID itemId, Principal principal) {
        cartService.removeCartItem(principal.getName(), itemId);
        return ResponseEntity.ok("Item removed from cart");
    }

    // Clear entire cart
    @DeleteMapping("/cart/clear")
    public ResponseEntity<String> clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return ResponseEntity.ok("Cart cleared");
    }

    // Checkout all items
    @PostMapping("/cart/checkout")
    public ResponseEntity<Order> checkout(Principal principal) {
        return ResponseEntity.ok(orderService.checkout(principal.getName()));
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<Order>> getOrders(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.getOrders(principal.getName(), page, size));
    }


    @GetMapping("/orders/filter")
    public ResponseEntity<Page<Order>> filterRetailerOrders(
            Principal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(orderService.filterRetailerOrders(principal.getName(), category, startDate, endDate, page, size));
    }


    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(productService.searchProducts(keyword, page, size));
    }
}
