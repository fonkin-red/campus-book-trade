package com.booktrading.book_trading_backend.mapper;

import com.booktrading.book_trading_backend.entity.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnnouncementMapper {

    @Select("SELECT * FROM announcement WHERE id = #{id}")
    Announcement selectById(Integer id);

    @Select("SELECT * FROM announcement ORDER BY is_pinned DESC, create_time DESC")
    List<Announcement> selectAll();

    @Select("SELECT * FROM announcement WHERE is_pinned = 1 ORDER BY create_time DESC")
    List<Announcement> selectPinned();

    @Insert("INSERT INTO announcement (title, content, publisher_id, is_pinned) " +
            "VALUES (#{title}, #{content}, #{publisherId}, #{isPinned})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Announcement announcement);

    @Update("UPDATE announcement SET title = #{title}, content = #{content}, is_pinned = #{isPinned} WHERE id = #{id}")
    int updateById(Announcement announcement);

    @Delete("DELETE FROM announcement WHERE id = #{id}")
    int deleteById(Integer id);
}
