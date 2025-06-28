package com.b2bapp.grocery.controller;

import com.b2bapp.grocery.dto.CartItemRequestDTO;
import com.b2bapp.grocery.dto.CartItemResponseDTO;
import com.b2bapp.grocery.dto.OrderResponseDTO;
import com.b2bapp.grocery.mapper.OrderMapper;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/retailer/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;

    // Add product to cart
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody CartItemRequestDTO request) {
        cartService.addToCart(request.getRetailerEmail(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok("Product added to cart");
    }

    // View all cart items for a retailer (with product details)
    @GetMapping
    public ResponseEntity<Page<CartItemResponseDTO>> getCartItems(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(cartService.getCartItemDTOs(principal.getName(), page, size));
    }



    // Remove specific item from cart
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<String> removeCartItem(
            @PathVariable UUID itemId,
            Principal principal) {
        cartService.removeCartItem(principal.getName(), itemId);
        return ResponseEntity.ok("Item removed from cart");
    }

    // Clear entire cart
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return ResponseEntity.ok("Cart cleared");
    }

    // Checkout all items
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(Principal principal) {
        Order order = orderService.checkout(principal.getName());
        return ResponseEntity.ok(OrderMapper.toOrderDTO(order));
    }



}
