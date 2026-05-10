package com.booktrading.book_trading_backend.controller;

import com.booktrading.book_trading_backend.dto.BookRequest;
import com.booktrading.book_trading_backend.dto.Result;
import com.booktrading.book_trading_backend.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) Integer categoryId,
                          @RequestParam(required = false) Long sellerId,
                          @RequestParam(required = false) Integer page,
                          @RequestParam(required = false) Integer pageSize,
                          HttpServletRequest request) {
        if (sellerId != null) {
            return Result.ok(bookService.listBySeller(sellerId));
        }
        return Result.ok(bookService.list(categoryId, page, pageSize));
    }

    @GetMapping("/search")
    public Result<?> search(@RequestParam String keyword) {
        return Result.ok(bookService.search(keyword));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.ok(bookService.getById(id));
    }

    @PostMapping
    public Result<?> publish(@Valid @RequestBody BookRequest req, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(bookService.publish(req, userId));
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody BookRequest req, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        bookService.update(id, req, userId);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        bookService.delete(id, userId);
        return Result.ok();
    }
}
