package com.booktrading.book_trading_backend.controller;

import com.booktrading.book_trading_backend.dto.Result;
import com.booktrading.book_trading_backend.entity.Announcement;
import com.booktrading.book_trading_backend.entity.BookCategory;
import com.booktrading.book_trading_backend.entity.User;
import com.booktrading.book_trading_backend.mapper.AnnouncementMapper;
import com.booktrading.book_trading_backend.mapper.BookCategoryMapper;
import com.booktrading.book_trading_backend.mapper.OrderMapper;
import com.booktrading.book_trading_backend.mapper.UserMapper;
import com.booktrading.book_trading_backend.service.AnnouncementService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserMapper userMapper;
    private final BookCategoryMapper categoryMapper;
    private final OrderMapper orderMapper;
    private final AnnouncementService announcementService;

    public AdminController(UserMapper userMapper, BookCategoryMapper categoryMapper,
                           OrderMapper orderMapper, AnnouncementService announcementService) {
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
        this.orderMapper = orderMapper;
        this.announcementService = announcementService;
    }

    // ==================== 用户管理 ====================

    /** 用户列表 */
    @GetMapping("/users")
    public Result<?> userList() {
        List<User> users = userMapper.selectAll();
        // 脱敏：移除密码字段
        users.forEach(u -> u.setPassword(null));
        return Result.ok(users);
    }

    /** 更新用户状态（启用/禁用） */
    @PutMapping("/users/{id}/status")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestBody User req) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setStatus(req.getStatus());
        userMapper.updateById(user);
        return Result.ok();
    }

    // ==================== 分类管理 ====================

    /** 分类列表 */
    @GetMapping("/categories")
    public Result<?> categoryList() {
        return Result.ok(categoryMapper.selectAll());
    }

    /** 添加分类 */
    @PostMapping("/category")
    public Result<?> addCategory(@RequestBody BookCategory category) {
        categoryMapper.insert(category);
        return Result.ok(category);
    }

    /** 更新分类 */
    @PutMapping("/category/{id}")
    public Result<?> updateCategory(@PathVariable Integer id, @RequestBody BookCategory category) {
        category.setId(id);
        categoryMapper.updateById(category);
        return Result.ok();
    }

    /** 删除分类 */
    @DeleteMapping("/category/{id}")
    public Result<?> deleteCategory(@PathVariable Integer id) {
        categoryMapper.deleteById(id);
        return Result.ok();
    }

    // ==================== 订单管理 ====================

    /** 全部订单 */
    @GetMapping("/orders")
    public Result<?> orderList() {
        return Result.ok(orderMapper.selectAll());
    }

    /** 发货（复用 OrderController 的逻辑，这里简化处理） */
    @PutMapping("/orders/{id}/ship")
    public Result<?> shipOrder(@PathVariable Long id) {
        orderMapper.updateStatusOnly(id, 2);
        return Result.ok();
    }

    // ==================== 公告管理 ====================

    /** 公告列表 */
    @GetMapping("/announcements")
    public Result<?> announcementList() {
        return Result.ok(announcementService.list());
    }

    /** 发布公告 */
    @PostMapping("/announcement")
    public Result<?> publishAnnouncement(@RequestBody Announcement announcement,
                                         HttpServletRequest request) {
        Long publisherId = (Long) request.getAttribute("userId");
        return Result.ok(announcementService.publish(announcement, publisherId));
    }

    /** 更新公告 */
    @PutMapping("/announcement/{id}")
    public Result<?> updateAnnouncement(@PathVariable Integer id,
                                        @RequestBody Announcement announcement) {
        try {
            announcementService.update(id, announcement);
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /** 删除公告 */
    @DeleteMapping("/announcement/{id}")
    public Result<?> deleteAnnouncement(@PathVariable Integer id) {
        announcementService.delete(id);
        return Result.ok();
    }
}