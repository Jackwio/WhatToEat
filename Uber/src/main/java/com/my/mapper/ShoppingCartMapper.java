package com.my.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.pojo.ShoppingCart;

public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
    void addCart(ShoppingCart shoppingCart);

}
