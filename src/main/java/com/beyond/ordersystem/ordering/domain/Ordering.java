package com.beyond.ordersystem.ordering.domain;

import com.beyond.ordersystem.common.domain.BaseTimeEntity;
import com.beyond.ordersystem.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ordering extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.ORDERED;

    //  board 에서 가입과 동시에 가입인사 post 를 생성한 것처럼.
    //  Ordering save 할 때 OrderDetail 도 같이 save 할 수 있게 하는 조건
    @OneToMany(mappedBy = "ordering", cascade = CascadeType.PERSIST)
    //  builder 패턴에서도 초기화 되도록 하는 설정
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
