package com.beyond.ordersystem.product.controller;

import com.beyond.ordersystem.common.dto.CommonErrorDto;
import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.product.domain.Product;
import com.beyond.ordersystem.product.dto.ProductSaveReqDto;
import com.beyond.ordersystem.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    private final ProductService productService;

    @Autowired
    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/product/create")
    public ResponseEntity<?> productCreate(ProductSaveReqDto dto) {
        try {
            Product product = productService.productCreate(dto);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Member is successfully created", product.getId());
            return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), "BAD REQUEST"), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/product/awscreate")
    public ResponseEntity<?> productAwsCreate(ProductSaveReqDto dto) {
        try {
            Product product = productService.productCreate(dto);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Member is successfully created", product.getId());
            return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), "BAD REQUEST"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/product/list")
    public ResponseEntity<?> productList(Pageable pageable) {
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "정상조회완료", productService.productList(pageable)), HttpStatus.OK);
    }
}
