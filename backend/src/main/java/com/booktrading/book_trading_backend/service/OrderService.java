package com.booktrading.book_trading_backend.service;

import com.booktrading.book_trading_backend.entity.Book;
import com.booktrading.book_trading_backend.entity.OrderInfo;
import com.booktrading.book_trading_backend.dto.OrderRequest;
import com.booktrading.book_trading_backend.mapper.BookMapper;
import com.booktrading.book_trading_backend.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final BookMapper bookMapper;

    public OrderService(OrderMapper orderMapper, BookMapper bookMapper) {
        this.orderMapper = orderMapper;
        this.bookMapper = bookMapper;
    }

    /** 创建订单 */
    @Transactional
    public OrderInfo create(OrderRequest req, Long buyerId) {
        Book book = bookMapper.selectById(req.getBookId());
        if (book == null || book.getStatus() != 1) {
            throw new RuntimeException("图书不存在或已下架");
        }
        if (book.getSellerId().equals(buyerId)) {
            throw new RuntimeException("不能购买自己发布的图书");
        }
        int quantity = req.getQuantity() != null ? req.getQuantity() : 1;
        BigDecimal total = book.getPrice().multiply(BigDecimal.valueOf(quantity));

        OrderInfo order = new OrderInfo();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", "").substring(0, 20));
        order.setBuyerId(buyerId);
        order.setSellerId(book.getSellerId());
        order.setBookId(book.getId());
        order.setBookTitle(book.getTitle());
        order.setBookPrice(book.getPrice());
        order.setQuantity(quantity);
        order.setTotalAmount(total);
        order.setContactInfo(req.getContactInfo());
        order.setDeliveryAddress(req.getDeliveryAddress());
        order.setRemark(req.getRemark());
        order.setStatus(0); // 待付款
        orderMapper.insert(order);

        return order;
    }

    /** 买家订单列表 */
    public List<OrderInfo> listByBuyer(Long buyerId) {
        return orderMapper.selectByBuyer(buyerId);
    }

    /** 卖家订单列表 */
    public List<OrderInfo> listBySeller(Long sellerId) {
        return orderMapper.selectBySeller(sellerId);
    }

    /** 订单详情 */
    public OrderInfo getById(Long id, Long userId, Integer role) {
        OrderInfo order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        boolean isAdmin = role != null && role == 1;
        if (!isAdmin && !order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权查看该订单");
        }
        return order;
    }

    /** 付款 */
    @Transactional
    public void pay(Long id, Long buyerId) {
        OrderInfo order = orderMapper.selectById(id);
        if (order == null || !order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("无权操作");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不允许付款");
        }
        Book book = bookMapper.selectById(order.getBookId());
        if (book == null || book.getStatus() != 1) {
            throw new RuntimeException("图书不存在或已下架");
        }
        order.setStatus(1);
        order.setPaymentTime(LocalDateTime.now());
        orderMapper.updateStatus(order);
        book.setStatus(2);
        bookMapper.updateById(book);
    }

    /** 发货 */
    public void ship(Long id, Long sellerId) {
        OrderInfo order = orderMapper.selectById(id);
        if (order == null || !order.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权操作");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不允许发货");
        }
        order.setStatus(2);
        orderMapper.updateStatus(order);
    }

    /** 确认收货 */
    public void confirm(Long id, Long buyerId) {
        OrderInfo order = orderMapper.selectById(id);
        if (order == null || !order.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("无权操作");
        }
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态不允许确认收货");
        }
        order.setStatus(3);
        order.setCompleteTime(LocalDateTime.now());
        orderMapper.updateStatus(order);
    }

    /** 取消订单 */
    @Transactional
    public void cancel(Long id, Long userId) {
        OrderInfo order = orderMapper.selectById(id);
        if (order == null || (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId))) {
            throw new RuntimeException("无权操作");
        }
        if (order.getStatus() >= 3) {
            throw new RuntimeException("订单已完成，无法取消");
        }
        Integer oldStatus = order.getStatus();
        order.setStatus(4);
        orderMapper.updateStatus(order);

        // 已付款或已发货的订单取消时，恢复图书为在售状态
        Book book = bookMapper.selectById(order.getBookId());
        if (oldStatus != null && oldStatus >= 1 && book != null && book.getStatus() == 2) {
            book.setStatus(1);
            bookMapper.updateById(book);
        }
    }

    /** 管理员发货 */
    public void adminShip(Long id) {
        OrderInfo order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不允许发货");
        }
        order.setStatus(2);
        orderMapper.updateStatus(order);
    }

    /** 管理员：获取全部订单 */
    public List<OrderInfo> listAll() {
        return orderMapper.selectAll();
    }
}
