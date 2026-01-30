package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.Order;
import com.thantruongnhan.doanketthucmon.entity.Product;
import com.thantruongnhan.doanketthucmon.entity.enums.OrderStatus;
import com.thantruongnhan.doanketthucmon.entity.enums.PaymentMethod;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Order createOrder(Order order);

    Order updateOrder(Long id, OrderStatus status, PaymentMethod paymentMethod);

    void deleteOrder(Long id);

    Order getOrderById(Long id);

    List<Order> getAllOrders();

    Order addProductToOrder(Long orderId, Product product, int quantity);

    List<Order> searchOrders(String keyword);

    Order updateOrderStatus(Long id, OrderStatus status);

    Order addMultipleProductsToOrder(Long orderId, List<Map<String, Object>> newItems);

}
