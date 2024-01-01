package com.my.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.my.pojo.ShoppingCartElements;

import java.util.List;

public interface ShoppingCartElementsService extends IService<ShoppingCartElements> {
    void addCartItem(ShoppingCartElements cartItem);

    void updateCartItem(ShoppingCartElements cartItem, Integer mId);

    List<ShoppingCartElements> getAllCartItem(Integer cId);

    ShoppingCartElements getCartItemByMid(Integer mId);
}
