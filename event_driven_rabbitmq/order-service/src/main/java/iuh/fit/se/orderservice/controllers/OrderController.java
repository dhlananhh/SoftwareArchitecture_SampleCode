package iuh.fit.se.orderservice.controllers;

import iuh.fit.se.orderservice.dtos.OrderRequest;
import iuh.fit.se.orderservice.enums.OrderStatus;
import iuh.fit.se.orderservice.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private OrderService orderService;

    @Autowired
    public OrderController (OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        System.out.println(orderRequest);
        orderService.createOrder(orderRequest);
        logger.info("Order created");

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getOrderById(orderId));
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable Long orderId,
                                         @RequestParam(required = false) String orderStatus) {

        logger.info("Update order " + orderId + " status: " + orderStatus);
        orderService.updateOrder(
                orderId,
                OrderStatus.fromValue(orderStatus)
        );

        return ResponseEntity.noContent().build();
    }
}
