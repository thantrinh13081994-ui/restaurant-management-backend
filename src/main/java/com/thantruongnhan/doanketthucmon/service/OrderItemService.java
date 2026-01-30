package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.OrderItem;

import java.util.List;

public interface OrderItemService {
    List<OrderItem> getAllItems();

    OrderItem getItemById(Long id);

    OrderItem createItem(OrderItem item);

    OrderItem updateItem(Long id, OrderItem item);

    void deleteItem(Long id);
}
