package com.booktrading.book_trading_backend.controller;

import com.booktrading.book_trading_backend.dto.Result;
import com.booktrading.book_trading_backend.mapper.BookCategoryMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
public class BookCategoryController {

    private final BookCategoryMapper categoryMapper;

    public BookCategoryController(BookCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(categoryMapper.selectAll());
    }
}
