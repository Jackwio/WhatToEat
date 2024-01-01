package com.my.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper extends BaseMapper<Order> {
    void saveOrder(Order order);

    List<Order> getOrders(Integer restId);

    List<Order> getOrderByState(@Param("restId") Integer restId, @Param("state") int state);

    List<Order> geAllOrder();

    List<Order> getOrderByEamil(String memEmail);

    List<Order> getOrderByOrderIdAndRestAccepted(Integer orderId, @Param("restAccepted") int i);

    List<Order> getOrdersByEmailAndRestAccepted(String memEmail, int state);

    List<Order> getOrderByRestState(int state);
}
