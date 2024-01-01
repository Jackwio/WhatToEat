package com.my.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.pojo.Member;
import com.my.pojo.Order;
import com.my.pojo.ShoppingCartElements;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.mockito.internal.matchers.Or;

import java.util.List;

public interface OrderService extends IService<Order> {
    void addCart(Integer mId, HttpSession session);
    void editCart(Integer mId,HttpSession session,Integer change);
    void delteCart(Integer mId, HttpSession session);
    void changeCart(Integer mId,HttpSession session,Integer change);
    void order(HttpSession session);
    Integer calculateAll(List<ShoppingCartElements> cartItems);

    void goToPay(HttpSession session, HttpServletRequest request);

    //轉換orders成詳細orders
    List<Order> getOrderItem(List<Order> orders);

    void getCommentOrder(Member member, HttpServletRequest request,HttpSession session);
}
