package com.b2bapp.grocery.service.impl;

import com.b2bapp.grocery.dto.CartItemResponseDTO;
import com.b2bapp.grocery.exception.ResourceNotFoundException;
import com.b2bapp.grocery.model.*;
import com.b2bapp.grocery.repository.*;
import com.b2bapp.grocery.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;


    @Override
    public CartItem addToCart(String retailerEmail, UUID productId, int quantity) {
        User retailer = userRepository.findByEmail(retailerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItem cartItem = CartItem.builder()
                .retailer(retailer)
                .product(product)
                .quantity(quantity)
                .build();

        return cartItemRepository.save(cartItem);
    }

    @Override
    public Page<CartItemResponseDTO> getCartItemDTOs(String retailerEmail, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<CartItem> items = cartItemRepository.findByRetailerEmail(retailerEmail, pageable);

        return items.map(item -> CartItemResponseDTO.builder()
                .cartItemId(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .imageUrls(item.getProduct().getImageUrls())
                .brand(item.getProduct().getBrand())
                .price(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .build());
    }




    @Override
    public void removeCartItem(String retailerEmail, UUID itemId) {
        User retailer = userRepository.findByEmail(retailerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found"));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getRetailer().getId().equals(retailer.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void clearCart(String retailerEmail) {
        User retailer = userRepository.findByEmail(retailerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found"));
        cartItemRepository.deleteByRetailer(retailer);
    }

    @Override
    @Transactional
    public void checkout(String retailerEmail) {
        User retailer = userRepository.findByEmail(retailerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found"));

        List<CartItem> cartItems = cartItemRepository.findByRetailer(retailer);

        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }

        // Create new order
        Order order = Order.builder()
                .retailer(retailer)
                .totalAmount(cartItems.stream()
                        .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                        .sum())
                .build();
        orderRepository.save(order);

        // Create order items
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            return OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(cartItem.getProduct().getPrice())
                    .build();
        }).toList();

        orderItemRepository.saveAll(orderItems);

        // Clear cart after successful checkout
        cartItemRepository.deleteAll(cartItems);
    }

}
