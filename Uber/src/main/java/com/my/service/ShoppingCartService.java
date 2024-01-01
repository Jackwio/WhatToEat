package com.my.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.pojo.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    void addCart(ShoppingCart shoppingCart);
}
