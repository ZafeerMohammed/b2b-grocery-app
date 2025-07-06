package com.b2bapp.grocery.service.impl;

import com.b2bapp.grocery.dto.ReturnRequestDTO;
import com.b2bapp.grocery.dto.ReturnRequestResponseDTO;
import com.b2bapp.grocery.dto.ReturnStatsDTO;
import com.b2bapp.grocery.dto.TopReturnedProductDTO;
import com.b2bapp.grocery.exception.ResourceNotFoundException;
import com.b2bapp.grocery.mapper.ReturnRequestMapper;
import com.b2bapp.grocery.model.*;
import com.b2bapp.grocery.repository.OrderItemRepository;
import com.b2bapp.grocery.repository.ReturnRequestRepository;
import com.b2bapp.grocery.repository.UserRepository;
import com.b2bapp.grocery.service.EmailService;
import com.b2bapp.grocery.service.NotificationService;
import com.b2bapp.grocery.service.ReturnRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReturnRequestServiceImpl implements ReturnRequestService {

    private final ReturnRequestRepository returnRepo;
    private final UserRepository userRepo;
    private final OrderItemRepository orderItemRepo;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    public ReturnRequestResponseDTO createReturnRequest(String retailerEmail, ReturnRequestDTO dto) {
        User retailer = userRepo.findByEmail(retailerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found"));

        OrderItem orderItem = orderItemRepo.findById(dto.getOrderItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        if (!orderItem.getOrder().getRetailer().getEmail().equals(retailerEmail)) {
            throw new SecurityException("Cannot return item not owned by this retailer");
        }

        if (dto.getQuantity() > orderItem.getQuantity()) {
            throw new IllegalArgumentException("Cannot return more than purchased");
        }

        ReturnRequest request = ReturnRequest.builder()
                .orderItem(orderItem)
                .retailer(retailer)
                .reason(dto.getReason())
                .quantity(dto.getQuantity())
                .status(ReturnStatus.REQUESTED)
                .requestDate(java.time.LocalDateTime.now())
                .build();

        // Notification
        User wholesaler = orderItem.getProduct().getWholesaler();

        notificationService.notifyUser(
                wholesaler,
                "New return request received",
                "Retailer " + retailer.getName() + " has requested a return for " +
                        orderItem.getProduct().getName() + " (" + dto.getQuantity() + " units)."
        );

        // 📧 Email to wholesaler
        String subject = "Return Request from Retailer - " + retailer.getName();
        String body = "<p>Hi " + wholesaler.getName() + ",</p>" +
                "<p>You have received a new return request for the product <strong>" +
                orderItem.getProduct().getName() + "</strong>.</p>" +
                "<p><strong>Retailer:</strong> " + retailer.getName() + "<br/>" +
                "<strong>Quantity:</strong> " + dto.getQuantity() + "<br/>" +
                "<strong>Reason:</strong> " + dto.getReason() + "</p>" +
                "<p>Please take appropriate action.</p>";

        emailService.sendEmail(wholesaler.getEmail(), subject, body);

        return ReturnRequestMapper.toDTO(returnRepo.save(request));
    }

    @Override
    public Page<ReturnRequestResponseDTO> getReturnsForRetailer(String retailerEmail, int page, int size) {
        User retailer = userRepo.findByEmail(retailerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Retailer not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("requestDate").descending());

        return returnRepo.findByRetailer(retailer, pageable)
                .map(ReturnRequestMapper::toDTO);
    }

    @Override
    public Page<ReturnRequestResponseDTO> getReturnsForWholesaler(String wholesalerEmail, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestDate").descending());

        return returnRepo.findByOrderItem_Product_Wholesaler_Email(wholesalerEmail, pageable)
                .map(ReturnRequestMapper::toDTO);
    }

    @Override
    public Page<ReturnRequestResponseDTO> getAllReturns(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestDate").descending());

        return returnRepo.findAll(pageable).map(ReturnRequestMapper::toDTO);
    }

    @Override
    public Page<ReturnRequestResponseDTO> getReturnsByStatus(ReturnStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestDate").descending());

        return returnRepo.findByStatus(status, pageable).map(ReturnRequestMapper::toDTO);
    }

    @Override
    public void updateReturnStatus(UUID returnId, ReturnStatus newStatus) {
        ReturnRequest request = returnRepo.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found"));

        request.setStatus(newStatus);
        returnRepo.save(request);

        notifyReturnStatusChange(request);
    }



    @Override
    public void updateReturnStatusByWholesaler(UUID returnId, ReturnStatus newStatus, String wholesalerEmail) {
        ReturnRequest request = returnRepo.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request not found"));

        if (!request.getOrderItem().getProduct().getWholesaler().getEmail().equalsIgnoreCase(wholesalerEmail)) {
            throw new AccessDeniedException("Not authorized to update this return");
        }

        request.setStatus(newStatus);
        returnRepo.save(request);

        notifyReturnStatusChange(request);
    }


    private void notifyReturnStatusChange(ReturnRequest request) {
        User retailer = request.getRetailer();
        String productName = request.getOrderItem().getProduct().getName();
        ReturnStatus status = request.getStatus();

        // In-app Notification
        notificationService.notifyUser(
                retailer,
                "Return status updated",
                "Your return request for " + productName + " is now marked as " + status.name()
        );

        // Email
        String subject = "Return Request Status Updated";
        String body = "<p>Hi " + retailer.getName() + ",</p>" +
                "<p>Your return request for <strong>" + productName + "</strong> has been updated to: <strong>" + status.name() + "</strong>.</p>" +
                "<p>Thank you for using B2B Grocery App.</p>";

        emailService.sendEmail(retailer.getEmail(), subject, body);
    }

    @Override
    public ReturnStatsDTO getReturnStatsForWholesaler(String email, LocalDate startDate, LocalDate endDate) {
        List<ReturnRequest> returns = returnRepo.findByOrderItem_Product_Wholesaler_Email(email);

        returnRepo.flush(); // Optional: if needed for fresh data

        // Filter by date range and delivered orders only
        returns = returns.stream()
                .filter(r -> {
                    LocalDate requestDate = r.getRequestDate().toLocalDate();
                    return (startDate == null || !requestDate.isBefore(startDate)) &&
                            (endDate == null || !requestDate.isAfter(endDate));
                })
                .toList();

        long total = returns.size();
        long requested = returns.stream().filter(r -> r.getStatus() == ReturnStatus.REQUESTED).count();
        long approved = returns.stream().filter(r -> r.getStatus() == ReturnStatus.APPROVED).count();
        long rejected = returns.stream().filter(r -> r.getStatus() == ReturnStatus.REJECTED).count();

        return new ReturnStatsDTO(total, requested, approved, rejected);
    }



    @Override
    public List<TopReturnedProductDTO> getTopReturnedProductsForWholesaler(String email, LocalDate startDate, LocalDate endDate) {
        List<ReturnRequest> returns = returnRepo.findByOrderItem_Product_Wholesaler_Email(email);

        // Filter by date
        returns = returns.stream()
                .filter(r -> {
                    LocalDate d = r.getRequestDate().toLocalDate();
                    return (startDate == null || !d.isBefore(startDate)) &&
                            (endDate == null || !d.isAfter(endDate));
                })
                .filter(r -> r.getStatus() != ReturnStatus.REJECTED) // Optional
                .toList();

        // Aggregate by product
        Map<String, Long> returnCounts = new HashMap<>();

        for (ReturnRequest r : returns) {
            String productName = r.getOrderItem().getProduct().getName();
            returnCounts.put(productName,
                    returnCounts.getOrDefault(productName, 0L) + r.getQuantity());
        }

        return returnCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new TopReturnedProductDTO(e.getKey(), e.getValue()))
                .toList();
    }

}
