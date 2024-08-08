package com.beyond.ordersystem.ordering.repository;

import com.beyond.ordersystem.member.domain.Member;
import com.beyond.ordersystem.ordering.domain.Ordering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Ordering, Long> {
    List<Ordering> findByMember(Member member);
}