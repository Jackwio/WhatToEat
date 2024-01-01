package com.my.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartElements {
    @TableId(type = IdType.AUTO)
    private Integer cartItemId;
    private ShoppingCart cartId;
    private Menu foodId;
    private Integer foodNum;

    public ShoppingCartElements(Integer foodNum) {
        this.foodNum = foodNum;
    }

    public ShoppingCartElements(Menu foodId, Integer foodNum) {
        this.foodId = foodId;
        this.foodNum = foodNum;
    }
}
