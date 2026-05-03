package com.booktrading.book_trading_backend.mapper;

import com.booktrading.book_trading_backend.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User selectByEmail(String email);

    @Select("SELECT * FROM user ORDER BY create_time DESC")
    List<User> selectAll();

    @Insert("INSERT INTO user (username, password, nickname, phone, email, avatar, role, status) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{phone}, #{email}, #{avatar}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET password = #{password}, nickname = #{nickname}, phone = #{phone}, " +
            "email = #{email}, avatar = #{avatar}, status = #{status} WHERE id = #{id}")
    int updateById(User user);

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);
}
