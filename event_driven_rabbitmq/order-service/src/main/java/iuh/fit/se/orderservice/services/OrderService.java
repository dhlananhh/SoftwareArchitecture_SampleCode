package iuh.fit.se.orderservice.services;

import iuh.fit.se.orderservice.dtos.OrderRequest;
import iuh.fit.se.orderservice.entities.Order;
import iuh.fit.se.orderservice.enums.OrderStatus;

public interface OrderService {
    void createOrder(OrderRequest order);

    Order getOrderById(Long id);

    void updateOrder(Long orderId, OrderStatus status);
}
