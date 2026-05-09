package com.booktrading.book_trading_backend.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private Long bookId;
    private Integer quantity;
    private String contactInfo;
    private String deliveryAddress;
    private String remark;
}