package com.booktrading.book_trading_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Integer categoryId;
    private BigDecimal originalPrice;
    private BigDecimal price;
    private Integer condition;
    private String description;
    private String coverImage;
    private String images;
    private Long sellerId;
    private Integer status;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
