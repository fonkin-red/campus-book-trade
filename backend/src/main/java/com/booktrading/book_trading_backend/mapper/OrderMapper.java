package com.booktrading.book_trading_backend.mapper;

import com.booktrading.book_trading_backend.entity.OrderInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("SELECT * FROM order_info WHERE id = #{id}")
    OrderInfo selectById(Long id);

    @Select("SELECT * FROM order_info WHERE order_no = #{orderNo}")
    OrderInfo selectByOrderNo(String orderNo);

    @Select("SELECT * FROM order_info WHERE buyer_id = #{buyerId} ORDER BY create_time DESC")
    List<OrderInfo> selectByBuyer(Long buyerId);

    @Select("SELECT * FROM order_info WHERE seller_id = #{sellerId} ORDER BY create_time DESC")
    List<OrderInfo> selectBySeller(Long sellerId);

    @Select("SELECT * FROM order_info ORDER BY create_time DESC")
    List<OrderInfo> selectAll();

    @Insert("INSERT INTO order_info (order_no, buyer_id, seller_id, book_id, book_title, book_price, " +
            "quantity, total_amount, contact_info, delivery_address, status, remark) " +
            "VALUES (#{orderNo}, #{buyerId}, #{sellerId}, #{bookId}, #{bookTitle}, #{bookPrice}, " +
            "#{quantity}, #{totalAmount}, #{contactInfo}, #{deliveryAddress}, #{status}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderInfo order);

    @Update("UPDATE order_info SET status = #{status}, payment_time = #{paymentTime}, " +
            "complete_time = #{completeTime} WHERE id = #{id}")
    int updateStatus(OrderInfo order);

    @Update("UPDATE order_info SET status = #{status} WHERE id = #{id}")
    int updateStatusOnly(@Param("id") Long id, @Param("status") Integer status);
}
