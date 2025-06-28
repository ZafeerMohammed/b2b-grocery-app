package com.b2bapp.grocery.repository;

import com.b2bapp.grocery.model.Product;
import com.b2bapp.grocery.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findAllByActiveTrue(Pageable pageable);

    // Only fetch active products
    @Query("SELECT p FROM Product p WHERE p.wholesaler = :wholesaler AND p.active = true")
    Page<Product> findByWholesaler(@Param("wholesaler") User wholesaler, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.wholesaler = :wholesaler AND p.active = true")
    Page<Product> findByCategoryAndWholesaler(@Param("category") String category, @Param("wholesaler") User wholesaler, Pageable pageable);

    // Soft delete: use update instead of delete
    @Query("UPDATE Product p SET p.active = false WHERE LOWER(p.category) = LOWER(:category)")
    void softDeleteByCategoryIgnoreCase(@Param("category") String category);

    @Query("UPDATE Product p SET p.active = false WHERE p.wholesaler = :wholesaler")
    void softDeleteByWholesaler(@Param("wholesaler") User wholesaler);

    // Search only among active products for wholesaler
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
            "LOWER(p.wholesaler.email) = LOWER(:email) AND (" +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchByKeywordForWholesaler(@Param("email") String email,
                                               @Param("keyword") String keyword,
                                               Pageable pageable);


    // Search only among active products for Retailer
    @Query("SELECT p FROM Product p WHERE p.active = true AND (" +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchByKeywordForRetailer(@Param("keyword") String keyword, Pageable pageable);


    @Modifying
    @Query("UPDATE Product p SET p.active = false WHERE p.wholesaler = :wholesaler")
    void deactivateProductsByWholesaler(@Param("wholesaler") User wholesaler);


    @Modifying
    @Query("UPDATE Product p SET p.active = true WHERE p.wholesaler = :wholesaler")
    void activateProductsByWholesaler(@Param("wholesaler") User wholesaler);

}
