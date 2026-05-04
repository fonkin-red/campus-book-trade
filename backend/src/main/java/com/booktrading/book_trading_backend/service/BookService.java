package com.booktrading.book_trading_backend.service;

import com.booktrading.book_trading_backend.dto.BookRequest;
import com.booktrading.book_trading_backend.entity.Book;
import com.booktrading.book_trading_backend.mapper.BookMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookMapper bookMapper;

    public BookService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public Book getById(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        bookMapper.incrementViewCount(id);
        return book;
    }

    public List<Book> list(Integer categoryId, Integer page, Integer pageSize) {
        // 简单分页：mapper 层暂不写 LIMIT，先全量返回，后续优化
        if (categoryId != null) {
            return bookMapper.selectByCategory(categoryId);
        }
        return bookMapper.selectAllOnSale();
    }

    public List<Book> search(String keyword) {
        return bookMapper.searchByKeyword(keyword);
    }

    public Book publish(BookRequest req, Long sellerId) {
        Book book = new Book();
        book.setTitle(req.getTitle());
        book.setAuthor(req.getAuthor());
        book.setIsbn(req.getIsbn());
        book.setCategoryId(req.getCategoryId());
        book.setOriginalPrice(req.getOriginalPrice());
        book.setPrice(req.getPrice());
        book.setCondition(req.getCondition() != null ? req.getCondition() : 1);
        book.setDescription(req.getDescription());
        book.setCoverImage(req.getCoverImage());
        book.setImages(req.getImages());
        book.setSellerId(sellerId);
        book.setStatus(1);
        bookMapper.insert(book);
        return book;
    }

    public void update(Long id, BookRequest req, Long userId) {
        Book book = bookMapper.selectById(id);
        if (book == null || !book.getSellerId().equals(userId)) {
            throw new RuntimeException("无权编辑");
        }
        book.setTitle(req.getTitle());
        book.setAuthor(req.getAuthor());
        book.setIsbn(req.getIsbn());
        book.setCategoryId(req.getCategoryId());
        book.setOriginalPrice(req.getOriginalPrice());
        book.setPrice(req.getPrice());
        book.setCondition(req.getCondition());
        book.setDescription(req.getDescription());
        book.setCoverImage(req.getCoverImage());
        book.setImages(req.getImages());
        bookMapper.updateById(book);
    }

    public void delete(Long id, Long userId) {
        Book book = bookMapper.selectById(id);
        if (book == null || !book.getSellerId().equals(userId)) {
            throw new RuntimeException("无权删除");
        }
        bookMapper.deleteById(id);
    }

    public List<Book> listBySeller(Long sellerId) {
        return bookMapper.selectBySeller(sellerId);
    }
}
