package com.my.controller;

import com.my.pojo.Member;
import com.my.pojo.ShoppingCart;
import com.my.pojo.ShoppingCartElements;
import com.my.service.OrderService;
import com.my.service.RestaurantService;
import com.my.service.ShoppingCartElementsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private ShoppingCartElementsService shoppingCartElementsService;
    @Autowired
    private OrderService orderService;


    public RestaurantController() {
    }

    @RequestMapping("/lookRestaurant")
    public String lookRestaurant(HttpSession session, Integer pageNumber, String keyword, String action, String position,
                                 Integer time ,String menuType, String foodClass,
                                 Integer menuMoney, String menuConstraint, HttpServletRequest request) {

        restaurantService.lookRestaurant(session, pageNumber, keyword, action, position, time,
                 menuType, foodClass, menuMoney, menuConstraint);
        if (session.getAttribute("currentUser") != null) {
            Member currentUser = (Member) session.getAttribute("currentUser");
            if (currentUser.getMemType() == 0 && session.getAttribute("currentCommentOrder") == null) {
                orderService.getCommentOrder(currentUser, request, session);
            }
        }

        //回到main
        return "main";
    }

    @RequestMapping("/returnBack")
    public String returnBack(HttpSession session) {
        Member currentUser = (Member) session.getAttribute("currentUser");
        List<ShoppingCartElements> cartItems = new ArrayList<>();
        if (session.getAttribute("currentUser") != null) {
            cartItems = shoppingCartElementsService.getAllCartItem(currentUser.getCartId().getCartId());
        } else {
            for (ShoppingCartElements cartElements : ShoppingCart.tempCart.values()) {
                cartItems.add(cartElements);
            }
        }
        session.setAttribute("cartItems", cartItems);
        return "main";
    }

    @RequestMapping("/chooseType")
    public String chooseType(String type) {
        return "redirect:/lookRestaurant?menuType=" + type;
    }

    @RequestMapping("/useTurntable")
    public String useTurntable(HttpSession session) {
        restaurantService.useTurntable(session);
        return "turntable";
    }

    @RequestMapping("/getTurnTableResult")
    public String getTurnTableResult(Integer target, HttpSession session) {
        Integer restId = restaurantService.getTurnTableResult(target, session);
        return "forward:/lookMenu?restId=" + restId;
    }

}
