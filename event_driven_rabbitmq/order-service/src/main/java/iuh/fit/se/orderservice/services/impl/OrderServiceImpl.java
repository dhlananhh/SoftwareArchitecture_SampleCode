package iuh.fit.se.orderservice.services.impl;

import iuh.fit.se.orderservice.dtos.OrderMessage;
import iuh.fit.se.orderservice.dtos.OrderRequest;
import iuh.fit.se.orderservice.entities.Order;
import iuh.fit.se.orderservice.enums.OrderStatus;
import iuh.fit.se.orderservice.repositories.OrderRepository;
import iuh.fit.se.orderservice.services.OrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class OrderServiceImpl implements OrderService {
    @Value("${app.rabbit.queue-name}")
    private String queueName;
    @Value("${app.rabbit.exchange-name}")
    private String exchangeName;
    @Value("${app.rabbit.routing-key}")
    private String routingKey;

    private OrderRepository orderRepository;
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void createOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .userId(orderRequest.getUserId())
                .status(OrderStatus.ORDER_CREATED.getValue())
                .build();

        orderRepository.save(order);

        OrderMessage orderMessage = OrderMessage.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .orderStatus(order.getStatus())
                .build();

        rabbitTemplate.convertAndSend(exchangeName, routingKey, orderMessage);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(Long.valueOf(id)).orElseThrow(() -> new NoSuchElementException("Not found order id = " + id));
    }

    @Override
    public void updateOrder(Long orderId, OrderStatus status) {
        Order order = this.getOrderById(orderId);

        if (null == status || status.equals(OrderStatus.ORDER_CREATED) || order.getStatus().equals(status)) {
            return;
        }

        order.setStatus(status.getValue());
        orderRepository.save(order);

        OrderMessage orderMessage = OrderMessage.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .orderStatus(order.getStatus())
                .build();

        rabbitTemplate.convertAndSend(exchangeName, routingKey, orderMessage);
    }
}
