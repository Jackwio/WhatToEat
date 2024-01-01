package com.my.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.mapper.ShoppingCartMapper;
import com.my.pojo.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService{
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Override
    public void addCart(ShoppingCart shoppingCart) {
        shoppingCartMapper.addCart(shoppingCart);
    }
}
