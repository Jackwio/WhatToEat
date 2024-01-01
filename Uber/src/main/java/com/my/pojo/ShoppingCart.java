package com.my.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {

    @TableId(type = IdType.AUTO)
    private Integer cartId;
    private Member member;

    @TableField(exist = false)
    public static Map<Integer,ShoppingCartElements> tempCart;
}
