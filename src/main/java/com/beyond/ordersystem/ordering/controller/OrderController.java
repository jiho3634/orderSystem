package com.beyond.ordersystem.ordering.controller;

import com.beyond.ordersystem.common.dto.CommonErrorDto;
import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.ordering.domain.Ordering;
import com.beyond.ordersystem.ordering.dto.OrderSaveReqDto;
import com.beyond.ordersystem.ordering.service.OrderingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private final OrderingService orderingService;

    @Autowired
    public OrderController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }

    @PostMapping("/order/create")
    public ResponseEntity<?> orderCreate(@RequestBody List<OrderSaveReqDto> dto) {
        try {
            Ordering ordering = orderingService.orderCreate(dto);
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
                            , orderingService.orderList())
                    , HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/order/myorders")
    public ResponseEntity<?> myOrders() {
        try {
            return new ResponseEntity<>(
                    new CommonResDto(HttpStatus.OK
                            , "Order is successfully found"
                            , orderingService.myOrders())
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
                            , orderingService.orderCancel(id))
                    , HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
