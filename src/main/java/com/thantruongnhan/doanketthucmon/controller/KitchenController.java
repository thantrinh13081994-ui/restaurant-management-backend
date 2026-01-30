package com.thantruongnhan.doanketthucmon.controller;

import java.util.List;

import com.thantruongnhan.doanketthucmon.entity.OrderItem;
import com.thantruongnhan.doanketthucmon.entity.enums.KitchenStatus;
import com.thantruongnhan.doanketthucmon.service.KitchenService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/kitchen")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class KitchenController {

    private final KitchenService kitchenService;

    // Màn hình bếp
    @GetMapping("/queue")
    @PreAuthorize("hasAnyRole('KITCHEN','MANAGER','ADMIN')")
    public List<OrderItem> getKitchenQueue() {
        return kitchenService.getKitchenQueue();
    }

    // Update trạng thái món
    @PutMapping("/order-items/{id}/status")
    @PreAuthorize("hasAnyRole('KITCHEN','ADMIN')")
    public OrderItem updateStatus(
            @PathVariable Long id,
            @RequestParam KitchenStatus status) {

        return kitchenService.updateKitchenStatus(id, status);
    }
}
