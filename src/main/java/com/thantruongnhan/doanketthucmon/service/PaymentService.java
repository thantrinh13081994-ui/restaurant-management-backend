package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.Order;
import com.thantruongnhan.doanketthucmon.entity.Payment;
import com.thantruongnhan.doanketthucmon.entity.enums.OrderStatus;
import com.thantruongnhan.doanketthucmon.repository.OrderRepository;
import com.thantruongnhan.doanketthucmon.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Payment createPayment(Long orderId, Payment payment) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Gắn thanh toán vào đơn hàng
        payment.setOrder(order);
        payment.setPaymentDate(LocalDateTime.now());

        // Cập nhật đơn hàng
        order.setPaidAt(LocalDateTime.now());
        order.setStatus(OrderStatus.COMPLETED);
        order.setPayment(payment);

        orderRepository.save(order);
        return paymentRepository.save(payment);
    }

    public Payment getPaymentByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}
