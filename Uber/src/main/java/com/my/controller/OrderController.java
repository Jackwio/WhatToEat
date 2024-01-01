package com.my.controller;

import com.my.service.OrderService;
import com.my.service.ShoppingCartElementsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    public OrderController() {
    }

    //    加入購物車，更新內容
    @RequestMapping("/addCart")
    public String addCart(Integer mId, HttpSession session) {
        orderService.addCart(mId, session);
        //跳回餐廳菜單畫面
        return "menu::cartItemBlock";
    }

    //    編輯購物車數量
    @RequestMapping("/editCart")
    public String editCart(Integer mId, Integer change, HttpSession session) {
        orderService.editCart(mId, session, change);
        return "main::cartItemBlock";
    }

    //    更新購物車上數量
    @RequestMapping("/changeCartNumber")
    public String changeCartNumber(HttpSession session) {
        return "main::cartNumber";
    }

    //    刪除購物車東西
    @RequestMapping("/deleteCart")
    public String deleteCart(Integer mId, HttpSession session) {
        orderService.delteCart(mId, session);
        //跳回購物車頁面(右彈窗)
        return "main::cartItemBlock";
    }

    //    準備付款
    @RequestMapping("/goToPay")
    public String goToPay(HttpSession session, HttpServletRequest request) {

        orderService.goToPay(session, request);
        return "cart";
    }

    //    下單完成
    @RequestMapping("/order")
    public String Order(HttpSession session) {
        orderService.order(session);
        return "main";
    }
}
