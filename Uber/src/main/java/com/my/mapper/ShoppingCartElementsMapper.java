package com.my.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.pojo.ShoppingCartElements;

import java.util.List;

public interface ShoppingCartElementsMapper extends BaseMapper<ShoppingCartElements> {
    void addCartItem(ShoppingCartElements cartItem);

    void updateCartItem(ShoppingCartElements cartItem, Integer mId);

    List<ShoppingCartElements> getAllCartItem(Integer cId);

    ShoppingCartElements getCartItemByMid(Integer mId);
}
