package com.booktrading.book_trading_backend.service;

import com.booktrading.book_trading_backend.entity.Favorite;
import com.booktrading.book_trading_backend.entity.Book;
import com.booktrading.book_trading_backend.mapper.FavoriteMapper;
import com.booktrading.book_trading_backend.mapper.BookMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final BookMapper bookMapper;

    public FavoriteService(FavoriteMapper favoriteMapper, BookMapper bookMapper) {
        this.favoriteMapper = favoriteMapper;
        this.bookMapper = bookMapper;
    }

    /**
     * 获取用户收藏列表，关联查询图书信息（批量查询优化）
     */
    public List<Map<String, Object>> getFavorites(Long userId) {
        List<Favorite> favorites = favoriteMapper.selectByUserId(userId);
        if (favorites.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> bookIds = favorites.stream()
                .map(Favorite::getBookId)
                .collect(Collectors.toList());
        List<Book> books = bookMapper.selectByIds(bookIds);
        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Favorite favorite : favorites) {
            Book book = bookMap.get(favorite.getBookId());
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
        // 检查图书是否存在且未下架
        Book book = bookMapper.selectById(bookId);
        if (book == null || book.getStatus() == 0) {
            throw new RuntimeException("图书不存在或已下架");
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
        favorite.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(favorite);
    }

    /**
     * 检查是否已收藏
     */
    public boolean isFavorited(Long userId, Long bookId) {
        return favoriteMapper.selectByUserAndBook(userId, bookId) != null;
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
