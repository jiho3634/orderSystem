package com.beyond.ordersystem.ordering.dto;
import com.beyond.ordersystem.ordering.domain.OrderDetail;
import com.beyond.ordersystem.ordering.domain.OrderStatus;
import com.beyond.ordersystem.ordering.domain.Ordering;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResDto {
    private Long id;
    private String memberEmail;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Builder.Default
    private List<OrderDetailResDto> orderDetailResDtoList = new ArrayList<>();

    public OrderListResDto fromEntity(Ordering ordering) {
        return builder()
                .id(ordering.getId())
                .memberEmail(ordering.getMember().getEmail())
                .orderStatus(ordering.getOrderStatus())
                .orderDetailResDtoList(ordering.getOrderDetails().stream()
                        .map(orderDetail -> new OrderDetailResDto().fromEntity(orderDetail))
                        .collect(Collectors.toList()))
                .build();
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDetailResDto{
        private Long productId;
        private String productName;
        private Integer productCount;

        public OrderDetailResDto fromEntity(OrderDetail orderDetail) {
            return builder()
                    .productId(orderDetail.getId())
                    .productName(orderDetail.getProduct().getName())
                    .productCount(orderDetail.getQuantity())
                    .build();
        }
    }
}
