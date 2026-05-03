package com.booktrading.book_trading_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfo {
    private Long id;
    private String orderNo;
    private Long buyerId;
    private Long sellerId;
    private Long bookId;
    private String bookTitle;
    private BigDecimal bookPrice;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String contactInfo;
    private String deliveryAddress;
    private Integer status;
    private String remark;
    private LocalDateTime paymentTime;
    private LocalDateTime completeTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
