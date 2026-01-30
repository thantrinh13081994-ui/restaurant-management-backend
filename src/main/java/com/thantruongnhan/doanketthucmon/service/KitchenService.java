package com.thantruongnhan.doanketthucmon.service;

import java.util.List;

import com.thantruongnhan.doanketthucmon.entity.OrderItem;
import com.thantruongnhan.doanketthucmon.entity.enums.KitchenStatus;

public interface KitchenService {

    List<OrderItem> getKitchenQueue();

    OrderItem updateKitchenStatus(Long orderItemId, KitchenStatus status);
}
