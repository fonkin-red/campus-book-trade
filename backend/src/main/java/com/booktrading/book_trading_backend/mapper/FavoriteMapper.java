package com.booktrading.book_trading_backend.mapper;

import com.booktrading.book_trading_backend.entity.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    @Select("SELECT * FROM favorite WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Favorite> selectByUserId(Long userId);

    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND book_id = #{bookId}")
    Favorite selectByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Select("SELECT COUNT(*) FROM favorite WHERE user_id = #{userId}")
    int countByUserId(Long userId);

    @Insert("INSERT INTO favorite (user_id, book_id) VALUES (#{userId}, #{bookId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Favorite favorite);

    @Delete("DELETE FROM favorite WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND book_id = #{bookId}")
    int deleteByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
