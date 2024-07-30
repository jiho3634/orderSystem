package com.beyond.ordersystem.ordering.controller;

import com.beyond.ordersystem.common.dto.CommonErrorDto;
import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.ordering.domain.Ordering;
import com.beyond.ordersystem.ordering.dto.OrderListResDto;
import com.beyond.ordersystem.ordering.dto.OrderSaveReqDto;
import com.beyond.ordersystem.ordering.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order/create")
    public ResponseEntity<?> orderCreate(@RequestBody List<OrderSaveReqDto> dto) {
        try {
            Ordering ordering = orderService.orderCreate(dto);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Order is successfully created", ordering.getId());
            return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), "BAD REQUEST"), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/order/list")
    public ResponseEntity<?> orderList(Pageable pageable) {
        try {
            return new ResponseEntity<>(
                    new CommonResDto(HttpStatus.OK
                            , "Order is successfully found"
                            , orderService.orderList(pageable))
                    , HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/order/myorders")
    public ResponseEntity<?> myOrders(Pageable pageable) {
        try {
            return new ResponseEntity<>(
                    new CommonResDto(HttpStatus.OK
                            , "Order is successfully found"
                            , orderService.myOrders(pageable))
                    , HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/order/{id}/cancel")
    public ResponseEntity<?> orderCancel(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(
                    new CommonResDto(HttpStatus.OK
                            , "Order is successfully canceled"
                            , orderService.orderCancel(id))
                    , HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
