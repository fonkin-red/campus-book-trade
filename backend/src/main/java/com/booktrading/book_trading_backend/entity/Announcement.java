package com.booktrading.book_trading_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    private Integer id;
    private String title;
    private String content;
    private Long publisherId;
    private Integer isPinned;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
