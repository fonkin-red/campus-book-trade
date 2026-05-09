package com.booktrading.book_trading_backend.service;

import com.booktrading.book_trading_backend.entity.Favorite;
import com.booktrading.book_trading_backend.entity.Book;
import com.booktrading.book_trading_backend.mapper.FavoriteMapper;
import com.booktrading.book_trading_backend.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private BookMapper bookMapper;

    /**
     * 获取用户收藏列表，关联查询图书信息
     */
    public List<Map<String, Object>> getFavorites(Long userId) {
        List<Favorite> favorites = favoriteMapper.selectByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Favorite favorite : favorites) {
            Book book = bookMapper.selectById(favorite.getBookId());
            if (book != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", favorite.getId());
                item.put("bookId", book.getId());
                item.put("title", book.getTitle());
                item.put("coverImage", book.getCoverImage());
                item.put("price", book.getPrice());
                item.put("status", book.getStatus());
                item.put("createTime", favorite.getCreateTime());
                result.add(item);
            }
        }

        return result;
    }

    /**
     * 添加收藏
     */
    public void addFavorite(Long userId, Long bookId) {
        // 检查图书是否存在
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        // 检查是否已经收藏
        Favorite existFavorite = favoriteMapper.selectByUserAndBook(userId, bookId);
        if (existFavorite != null) {
            throw new RuntimeException("已经收藏过了");
        }

        // 添加收藏
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setBookId(bookId);
        favorite.setCreateTime(new Date());
        favoriteMapper.insert(favorite);
    }

    /**
     * 取消收藏
     */
    public void removeFavorite(Long userId, Long bookId) {
        Favorite existFavorite = favoriteMapper.selectByUserAndBook(userId, bookId);
        if (existFavorite == null) {
            throw new RuntimeException("收藏不存在");
        }
        favoriteMapper.deleteByUserAndBook(userId, bookId);
    }
}
