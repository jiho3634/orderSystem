package com.beyond.ordersystem.ordering.service;

import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.member.domain.Member;
import com.beyond.ordersystem.member.repository.MemberRepository;
import com.beyond.ordersystem.ordering.domain.OrderStatus;
import com.beyond.ordersystem.ordering.domain.Ordering;
import com.beyond.ordersystem.ordering.domain.OrderDetail;
import com.beyond.ordersystem.ordering.dto.OrderListResDto;
import com.beyond.ordersystem.ordering.dto.OrderSaveReqDto;
import com.beyond.ordersystem.ordering.repository.OrderRepository;
import com.beyond.ordersystem.product.domain.Product;
import com.beyond.ordersystem.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Autowired
    OrderService(OrderRepository orderRepository
            , MemberRepository memberService
            , ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberService;
        this.productRepository = productRepository;
    }

    public Ordering orderCreate(List<OrderSaveReqDto> dtos) {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(() -> new EntityNotFoundException("member is not found"));
        Ordering ordering = Ordering.builder().member(member).build();
        for (OrderSaveReqDto dto : dtos) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("product is not found"));
            Integer quantity = dto.getProductCount();
            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("재고 부족");
            }
            product.updateStockQuantity(quantity);
            //  ordering 이 아직 save 가 되지 않았지만, JPA 가 생성 후에 인자로 넣어준다.
            ordering.getOrderDetails().add(OrderDetail.builder()
                            .ordering(ordering)
                            .product(product)
                            .quantity(quantity)
                            .build());
        }
        return orderRepository.save(ordering);
    }

    public Page<OrderListResDto> orderList(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(ordering -> new OrderListResDto().fromEntity(ordering));
    }

    public Page<OrderListResDto> myOrders(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("member is not found"));
        return orderRepository.findByMember(member, pageable)
                .map(ordering -> new OrderListResDto().fromEntity(ordering));
    }

    public Ordering orderCancel(Long id) {
        Ordering ordering = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("order is not found"));
        ordering.updateOrderStatus(OrderStatus.CANCELED);
        return ordering;
    }
}
