package com.b2bapp.grocery.service;

import com.b2bapp.grocery.dto.CartItemResponseDTO;
import com.b2bapp.grocery.model.CartItem;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface CartService {
    CartItem addToCart(String retailerEmail, UUID productId, int quantity);
    Page<CartItemResponseDTO> getCartItemDTOs(String retailerEmail, int page, int size);
    void removeCartItem(String retailerEmail, UUID itemId);
    void clearCart(String retailerEmail);
    void checkout(String retailerEmail);


}
