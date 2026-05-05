package com.booktrading.book_trading_backend.controller;

import com.booktrading.book_trading_backend.dto.Result;
import com.booktrading.book_trading_backend.entity.Cart;
import com.booktrading.book_trading_backend.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET /cart - 获取购物车列表
    @GetMapping
    public Result<?> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(cartService.list(userId));
    }

    // POST /cart - 添加商品到购物车
    @PostMapping
    public Result<?> add(@RequestBody Cart cart, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.add(cart, userId);
        return Result.ok();
    }

    // PUT /cart/{id} - 更新购物车项
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Cart cart, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            cartService.update(id, cart, userId);
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // DELETE /cart/{id} - 删除购物车项
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            cartService.delete(id, userId);
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
