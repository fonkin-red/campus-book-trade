package com.booktrading.book_trading_backend.mapper;

import com.booktrading.book_trading_backend.entity.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    @Select("SELECT c.* FROM cart c WHERE c.user_id = #{userId}")
    List<Cart> selectByUserId(Long userId);

    @Select("SELECT * FROM cart WHERE id = #{id}")
    Cart selectById(Long id);

    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND book_id = #{bookId}")
    Cart selectByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Select("SELECT COUNT(*) FROM cart WHERE user_id = #{userId}")
    int countByUserId(Long userId);

    @Insert("INSERT INTO cart (user_id, book_id, quantity, selected) VALUES (#{userId}, #{bookId}, #{quantity}, #{selected})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Cart cart);

    @Update("UPDATE cart SET quantity = #{quantity}, selected = #{selected} WHERE id = #{id}")
    int updateById(Cart cart);

    @Delete("DELETE FROM cart WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND book_id = #{bookId}")
    int deleteByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
