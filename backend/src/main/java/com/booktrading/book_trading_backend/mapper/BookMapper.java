package com.booktrading.book_trading_backend.mapper;

import com.booktrading.book_trading_backend.entity.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookMapper {

    @Select("SELECT * FROM book WHERE id = #{id}")
    Book selectById(Long id);

    @Select("<script>SELECT * FROM book WHERE id IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    List<Book> selectByIds(@Param("ids") List<Long> ids);

    @Select("SELECT * FROM book WHERE status = 1 ORDER BY create_time DESC")
    List<Book> selectAllOnSale();

    @Select("SELECT * FROM book WHERE category_id = #{categoryId} AND status = 1 ORDER BY create_time DESC")
    List<Book> selectByCategory(Integer categoryId);

    @Select("SELECT * FROM book WHERE seller_id = #{sellerId} AND status != 0 ORDER BY create_time DESC")
    List<Book> selectBySeller(Long sellerId);

    @Select("SELECT * FROM book WHERE (title LIKE CONCAT('%',#{keyword},'%') OR author LIKE CONCAT('%',#{keyword},'%')) AND status = 1")
    List<Book> searchByKeyword(String keyword);

    @Insert("INSERT INTO book (title, author, isbn, category_id, original_price, price, `condition`, " +
            "description, cover_image, images, seller_id, status) " +
            "VALUES (#{title}, #{author}, #{isbn}, #{categoryId}, #{originalPrice}, #{price}, #{condition}, " +
            "#{description}, #{coverImage}, #{images}, #{sellerId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Book book);

    @Update("UPDATE book SET title = #{title}, author = #{author}, isbn = #{isbn}, category_id = #{categoryId}, " +
            "original_price = #{originalPrice}, price = #{price}, `condition` = #{condition}, " +
            "description = #{description}, cover_image = #{coverImage}, images = #{images}, " +
            "status = #{status} WHERE id = #{id}")
    int updateById(Book book);

    @Update("UPDATE book SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);

    @Delete("DELETE FROM book WHERE id = #{id}")
    int deleteById(Long id);
}
