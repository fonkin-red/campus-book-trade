package com.booktrading.book_trading_backend.mapper;

import com.booktrading.book_trading_backend.entity.BookCategory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookCategoryMapper {

    @Select("SELECT * FROM book_category WHERE id = #{id}")
    BookCategory selectById(Integer id);

    @Select("SELECT * FROM book_category ORDER BY sort_order ASC")
    List<BookCategory> selectAll();

    @Insert("INSERT INTO book_category (name, sort_order) VALUES (#{name}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BookCategory category);

    @Update("UPDATE book_category SET name = #{name}, sort_order = #{sortOrder} WHERE id = #{id}")
    int updateById(BookCategory category);

    @Delete("DELETE FROM book_category WHERE id = #{id}")
    int deleteById(Integer id);
}
