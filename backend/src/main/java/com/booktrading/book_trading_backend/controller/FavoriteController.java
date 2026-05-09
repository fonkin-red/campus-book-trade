package com.booktrading.book_trading_backend.controller;

import com.booktrading.book_trading_backend.dto.Result;
import com.booktrading.book_trading_backend.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * 获取我的收藏列表
     */
    @GetMapping
    public Result<?> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(favoriteService.getFavorites(userId));
    }

    /**
     * 收藏图书
     */
    @PostMapping("/{bookId}")
    public Result<?> add(@PathVariable Long bookId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            favoriteService.addFavorite(userId, bookId);
            return Result.ok("收藏成功");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{bookId}")
    public Result<?> remove(@PathVariable Long bookId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            favoriteService.removeFavorite(userId, bookId);
            return Result.ok("已取消收藏");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
}
