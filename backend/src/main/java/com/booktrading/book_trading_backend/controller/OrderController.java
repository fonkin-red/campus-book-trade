package com.booktrading.book_trading_backend.controller;

import com.booktrading.book_trading_backend.dto.OrderRequest;
import com.booktrading.book_trading_backend.dto.Result;
import com.booktrading.book_trading_backend.entity.OrderInfo;
import com.booktrading.book_trading_backend.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 创建订单 */
    @PostMapping
    public Result<?> create(@RequestBody OrderRequest req, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            return Result.ok(orderService.create(req, userId));
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /** 订单列表（同时查询买家和卖家订单） */
    @GetMapping("/list")
    public Result<?> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<OrderInfo> buyerOrders = orderService.listByBuyer(userId);
        List<OrderInfo> sellerOrders = orderService.listBySeller(userId);

        // 合并去重，按创建时间倒序
        Map<Long, OrderInfo> map = new LinkedHashMap<>();
        for (OrderInfo o : buyerOrders) {
            map.put(o.getId(), o);
        }
        for (OrderInfo o : sellerOrders) {
            map.putIfAbsent(o.getId(), o);
        }
        List<OrderInfo> allOrders = new ArrayList<>(map.values());
        allOrders.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));

        return Result.ok(allOrders);
    }

    /** 订单详情 */
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        try {
            return Result.ok(orderService.getById(id));
        } catch (RuntimeException e) {
            return Result.error(404, e.getMessage());
        }
    }

    /** 付款 */
    @PutMapping("/{id}/pay")
    public Result<?> pay(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            orderService.pay(id, userId);
            return Result.ok("付款成功");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /** 发货 */
    @PutMapping("/{id}/ship")
    public Result<?> ship(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            orderService.ship(id, userId);
            return Result.ok("发货成功");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /** 确认收货 */
    @PutMapping("/{id}/confirm")
    public Result<?> confirm(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            orderService.confirm(id, userId);
            return Result.ok("已确认收货");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /** 取消订单 */
    @PutMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            orderService.cancel(id, userId);
            return Result.ok("已取消");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
}