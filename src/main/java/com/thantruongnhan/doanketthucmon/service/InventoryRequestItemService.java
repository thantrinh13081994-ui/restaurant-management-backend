package com.thantruongnhan.doanketthucmon.service;

import java.util.List;

import com.thantruongnhan.doanketthucmon.entity.InventoryRequestItem;

public interface InventoryRequestItemService {

    InventoryRequestItem addItem(Long requestId, Long ingredientId, Double quantity);

    InventoryRequestItem updateQuantity(Long itemId, Double quantity);

    void deleteItem(Long itemId);

    List<InventoryRequestItem> getItemsByRequest(Long requestId);
}
