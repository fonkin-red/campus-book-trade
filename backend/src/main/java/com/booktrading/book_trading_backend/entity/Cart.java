package com.booktrading.book_trading_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Long id;
    private Long userId;
    private Long bookId;
    private Integer quantity;
    private Integer selected;
    private LocalDateTime createTime;

    // ===== 新增：关联查询图书信息（不是数据库字段，只用于接收查询结果） =====
    private String bookName;   // 图书名称（来自 book 表的 title 字段）
    private String cover;      // 图书封面（来自 book 表的 cover 字段）
    private Double price;      // 图书价格（来自 book 表的 price 字段）
}
