package com.my.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.mapper.ShoppingCartElementsMapper;
import com.my.pojo.ShoppingCartElements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ShoppingCartElementsServiceImpl extends ServiceImpl<ShoppingCartElementsMapper, ShoppingCartElements> implements ShoppingCartElementsService{
    @Autowired
    private ShoppingCartElementsMapper shoppingCartElementsMapper;

    @Override
    public List<ShoppingCartElements> getAllCartItem(Integer cId) {
        return shoppingCartElementsMapper.getAllCartItem(cId);
    }

    @Override
    public void updateCartItem(ShoppingCartElements cartItem, Integer mId) {
        shoppingCartElementsMapper.updateCartItem(cartItem,mId);
    }

    @Override
    public void addCartItem(ShoppingCartElements cartItem) {
        shoppingCartElementsMapper.addCartItem(cartItem);
    }

    @Override
    public ShoppingCartElements getCartItemByMid(Integer mId) {
        return shoppingCartElementsMapper.getCartItemByMid(mId);
    }
}
