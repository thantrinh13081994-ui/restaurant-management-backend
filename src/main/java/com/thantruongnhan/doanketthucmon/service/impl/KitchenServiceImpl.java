package com.thantruongnhan.doanketthucmon.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.thantruongnhan.doanketthucmon.entity.OrderItem;
import com.thantruongnhan.doanketthucmon.entity.enums.KitchenStatus;
import com.thantruongnhan.doanketthucmon.entity.enums.OrderStatus;
import com.thantruongnhan.doanketthucmon.repository.OrderItemRepository;
import com.thantruongnhan.doanketthucmon.repository.OrderRepository;
import com.thantruongnhan.doanketthucmon.service.KitchenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KitchenServiceImpl implements KitchenService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<OrderItem> getKitchenQueue() {
        return orderItemRepository
                .findByKitchenStatusNotOrderByCreatedAtAsc(KitchenStatus.DONE);
    }

    @Override
    @Transactional
    public OrderItem updateKitchenStatus(Long orderItemId, KitchenStatus status) {

        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        item.setKitchenStatus(status);
        orderItemRepository.save(item);

        // Nếu tất cả món đã DONE → cập nhật order
        if (!orderItemRepository.existsByOrderIdAndKitchenStatusNot(
                item.getOrder().getId(), KitchenStatus.DONE)) {

            item.getOrder().setStatus(OrderStatus.COMPLETED);
            orderRepository.save(item.getOrder());
        }

        return item;
    }
}
