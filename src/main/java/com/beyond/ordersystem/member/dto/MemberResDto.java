package com.beyond.ordersystem.member.dto;

import com.beyond.ordersystem.common.domain.Address;
import com.beyond.ordersystem.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResDto {
    private String name;
    private String email;
    private String password;
    private Address address;

    public MemberResDto fromEntity(Member member) {
        return builder()
                .name(member.getName())
                .email(member.getEmail())
                .password(member.getPassword())
                .address(member.getAddress())
                .build();
    }
}
