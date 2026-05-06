package com.booktrading.book_trading_backend.service;

import com.booktrading.book_trading_backend.dto.BookRequest;
import com.booktrading.book_trading_backend.entity.Book;
import com.booktrading.book_trading_backend.mapper.BookMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookMapper bookMapper;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

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
        if (book == null) {
            throw new RuntimeException("无权删除（图书不存在）");
        }
        if (!book.getSellerId().equals(userId)) {
            throw new RuntimeException("无权删除（sellerId=" + book.getSellerId() + ", userId=" + userId + "）");
        }
        // 删除关联的封面图片文件
        deleteFile(book.getCoverImage());
        // 删除关联的附加图片文件
        if (book.getImages() != null && !book.getImages().isEmpty()) {
            for (String url : book.getImages().split(",")) {
                deleteFile(url.trim());
            }
        }
        book.setStatus(0);
        bookMapper.updateById(book);
    }

    private void deleteFile(String url) {
        if (url == null || url.isEmpty()) return;
        try {
            String filename = Paths.get(url).getFileName().toString();
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filename);
            Files.deleteIfExists(filePath);
            log.info("已清理文件: {}", filename);
        } catch (IOException e) {
            log.warn("清理文件失败: {}", url, e);
        }
    }

    public List<Book> listBySeller(Long sellerId) {
        return bookMapper.selectBySeller(sellerId);
    }
}
