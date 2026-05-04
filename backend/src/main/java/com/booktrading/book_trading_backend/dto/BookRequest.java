package com.booktrading.book_trading_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookRequest {
    @NotBlank(message = "书名不能为空")
    private String title;
    private String author;
    private String isbn;
    private Integer categoryId;
    private BigDecimal originalPrice;
    @NotNull(message = "售价不能为空")
    private BigDecimal price;
    private Integer condition;
    private String description;
    private String coverImage;
    private String images;
}
