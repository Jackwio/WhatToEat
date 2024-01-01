package com.my.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.my.Enum.OrderState;
import com.my.Enum.RestOrderState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @TableId(value = "order_id",type=IdType.ASSIGN_ID)
    private Long orderId;
    private Menu menuFoodId;
    private Integer orderFoodNum;
    private Integer orderTotalPrice;
    private LocalDateTime orderDateTime;
    private Member memEmail;
    private Integer restId;
    private RestOrderState restAccepted;
    private OrderState orderState;

    @TableField(exist = false)
    private ArrayList<ShoppingCartElements> orderItem = new ArrayList<>();
    @TableField(exist = false)
    private Integer orderTotal;
    @TableField(exist = false)
    private Restaurant rest;

    public Order(RestOrderState restAccepted) {
        this.restAccepted = restAccepted;
    }

    public Order(Long orderId, Menu menuFoodId, Integer orderFoodNum, Integer orderTotalPrice, LocalDateTime orderDateTime, Member memEmail, Integer restId, RestOrderState restAccepted, OrderState orderState) {
        this.orderId = orderId;
        this.menuFoodId = menuFoodId;
        this.orderFoodNum = orderFoodNum;
        this.orderTotalPrice = orderTotalPrice;
        this.orderDateTime = orderDateTime;
        this.memEmail = memEmail;
        this.restId = restId;
        this.restAccepted = restAccepted;
        this.orderState = orderState;
    }

    public Order(OrderState orderState) {
        this.orderState = orderState;
    }
}
