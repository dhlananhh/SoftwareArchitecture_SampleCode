package iuh.fit.se.orderservice.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderMessage {
    Long orderId;
    Long userId;
    String orderStatus;
}