package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.CartItemRequestDTO;
import com.b2bapp.grocery.model.CartItem;
import com.b2bapp.grocery.model.Order;
import com.b2bapp.grocery.service.CartService;
import com.b2bapp.grocery.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/retailer")
@RequiredArgsConstructor
public class RetailerController {

    private final CartService cartService;
    private final OrderService orderService;

    // Add product to cart
    @PostMapping("/cart/add")
    public ResponseEntity<String> addToCart(@RequestBody CartItemRequestDTO request) {
        cartService.addToCart(request.getRetailerEmail(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok("Product added to cart");
    }

    // View all cart items for a retailer
    @GetMapping("/cart")
    public ResponseEntity<List<CartItem>> getCartItems(@RequestParam String retailerEmail) {
        return ResponseEntity.ok(cartService.getCartItems(retailerEmail));
    }

    // Remove specific item from cart
    @DeleteMapping("/cart/remove/{itemId}")
    public ResponseEntity<String> removeCartItem(@PathVariable UUID itemId, @RequestParam String retailerEmail) {
        cartService.removeCartItem(retailerEmail, itemId);
        return ResponseEntity.ok("Item removed from cart");
    }

    // Clear entire cart
    @DeleteMapping("/cart/clear")
    public ResponseEntity<String> clearCart(@RequestParam String retailerEmail) {
        cartService.clearCart(retailerEmail);
        return ResponseEntity.ok("Cart cleared");
    }

    // Checkout all items
    @PostMapping("/cart/checkout")
    public ResponseEntity<Order> checkout(@RequestParam String retailerEmail) {
        return ResponseEntity.ok(orderService.checkout(retailerEmail));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrders(@RequestParam String retailerEmail) {
        return ResponseEntity.ok(orderService.getOrders(retailerEmail));
    }
}
