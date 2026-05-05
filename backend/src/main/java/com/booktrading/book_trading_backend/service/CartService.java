package com.booktrading.book_trading_backend.service;

import com.booktrading.book_trading_backend.entity.Cart;
import com.booktrading.book_trading_backend.mapper.CartMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartMapper cartMapper;

    public CartService(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    // 1. 获取购物车列表
    public List<Cart> list(Long userId) {
        return cartMapper.selectByUserId(userId);
    }

    // 2. 添加商品到购物车（如果已存在则累加数量）
    public void add(Cart cart, Long userId) {
        cart.setUserId(userId);
        Cart exist = cartMapper.selectByUserAndBook(userId, cart.getBookId());
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + (cart.getQuantity() != null ? cart.getQuantity() : 1));
            cartMapper.updateById(exist);
        } else {
            cart.setQuantity(cart.getQuantity() != null ? cart.getQuantity() : 1);
            cart.setSelected(1);
            cartMapper.insert(cart);
        }
    }

    // 3. 更新（修改数量、选中状态）
    public void update(Long id, Cart cart, Long userId) {
        // 先查询该购物车项是否存在且属于当前用户
        Cart exist = cartMapper.selectById(id);
        if (exist == null || !exist.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该购物车项");
        }
        cart.setId(id);
        cart.setUserId(userId);
        cartMapper.updateById(cart);
    }

    // 4. 删除
    public void delete(Long id, Long userId) {
        Cart exist = cartMapper.selectById(id);
        if (exist == null || !exist.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该购物车项");
        }
        cartMapper.deleteById(id);
    }
}
