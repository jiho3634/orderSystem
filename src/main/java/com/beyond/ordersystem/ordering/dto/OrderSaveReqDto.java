package com.beyond.ordersystem.ordering.dto;
import com.beyond.ordersystem.member.domain.Member;
import com.beyond.ordersystem.ordering.domain.Ordering;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSaveReqDto {
    private Long productId;
    private Integer productCount;

    public Ordering toEntity(Member member){
        return Ordering.builder()
                .member(member)
                .build();
    }
}