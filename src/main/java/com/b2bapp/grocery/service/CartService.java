package com.b2bapp.grocery.service;

import com.b2bapp.grocery.model.CartItem;

import java.util.List;
import java.util.UUID;

public interface CartService {
    CartItem addToCart(String retailerEmail, UUID productId, int quantity);
    List<CartItem> getCartItems(String retailerEmail);
    void removeCartItem(String retailerEmail, UUID itemId);
    void clearCart(String retailerEmail);
    void checkout(String retailerEmail);
}
