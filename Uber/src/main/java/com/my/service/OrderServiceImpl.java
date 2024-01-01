package com.my.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.Enum.OrderState;
import com.my.Enum.RestOrderState;
import com.my.mapper.RestaurantMapper;
import com.my.pojo.Ratings;
import com.my.mapper.MenuMapper;
import com.my.mapper.OrderMapper;
import com.my.mapper.RatingsMapper;
import com.my.pojo.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private ShoppingCartElementsService shoppingCartElementsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RatingsMapper ratingsMapper;
    @Autowired
    private RestaurantMapper restaurantMapper;

    @Override
    public void addCart(Integer mId, HttpSession session) {
        changeCart(mId, session, 1);
    }

    @Override
    public void editCart(Integer mId, HttpSession session, Integer change) {
        changeCart(mId, session, change);
    }

    @Override
    public void delteCart(Integer mId, HttpSession session) {
        Integer change = Integer.MAX_VALUE;
        changeCart(mId, session, change);
    }

    @Override
    public void changeCart(Integer mId, HttpSession session, Integer change) {

        Member currentUser;
        List<ShoppingCartElements> cartItems = new ArrayList<>();
        Menu menu = menuMapper.getMenuById(mId);

        if (session.getAttribute("currentUser") == null) {
            Map<Integer, ShoppingCartElements> tempCart = ShoppingCart.tempCart;

            if (!tempCart.containsKey(mId)) {
                tempCart.put(mId, new ShoppingCartElements(menu, 1));
            } else {
                if (change + tempCart.get(mId).getFoodNum() <= 0) {
                    tempCart.remove(mId);
                } else {
                    ShoppingCartElements cartElements = tempCart.get(mId);
                    cartElements.setFoodNum(cartElements.getFoodNum() + change);
                }
            }
            for (ShoppingCartElements cartElements : tempCart.values()) {
                cartItems.add(cartElements);
            }

        } else {
            currentUser = (Member) session.getAttribute("currentUser");
            //從購物車中尋找是否有該餐點
            LambdaQueryWrapper<ShoppingCartElements> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ShoppingCartElements::getCartId, currentUser.getCartId().getCartId())
                    .eq(ShoppingCartElements::getFoodId, mId);
            ShoppingCartElements cartItem = shoppingCartElementsService.getOne(wrapper);
            if (cartItem == null) {
                ShoppingCartElements item = new ShoppingCartElements
                        (null, currentUser.getCartId(), new Menu(mId), 1);
                shoppingCartElementsService.addCartItem(item);
            } else {
                if (change + cartItem.getFoodNum() <= 0) {
                    shoppingCartElementsService.removeById(cartItem.getCartItemId());
                } else {
                    shoppingCartElementsService.update(new ShoppingCartElements(cartItem.getFoodNum() + change), wrapper);
                }
            }
            cartItems = shoppingCartElementsService
                    .getAllCartItem(currentUser.getCartId().getCartId());
        }


        session.setAttribute("cartItems", cartItems);
    }

    @Override
    public void order(HttpSession session) {
        Member user = (Member) session.getAttribute("currentUser");
        LambdaQueryWrapper<ShoppingCartElements> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCartElements::getCartId, user.getCartId().getCartId());

        LocalDateTime tempNowTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss");
        String text = tempNowTime.format(formatter);
        LocalDateTime nowTime = LocalDateTime.parse(text, formatter);
        List<ShoppingCartElements> cartItems = shoppingCartElementsService.getAllCartItem(user.getCartId().getCartId());

        //在訂單中加入此次購買的東西
        ArrayList<Order> orders = new ArrayList<>();
        for (ShoppingCartElements cartItem : cartItems) {
            orders.add(new Order(null, cartItem.getFoodId(), cartItem.getFoodNum(), cartItem.getFoodId()
                    .getMenuFoodPrice() * cartItem.getFoodNum(), nowTime, user, cartItem.getFoodId().getRestId(), RestOrderState.UNACCEPT, OrderState.PAID));
        }
        for (Order order : orders) {
            orderMapper.saveOrder(order);
        }

        //刪掉購物車中全部內容
        shoppingCartElementsService.remove(wrapper);
        session.setAttribute("cartItems", new ArrayList<ShoppingCartElements>());
    }

    @Override
    public void goToPay(HttpSession session, HttpServletRequest request) {
        List<ShoppingCartElements> cartItems = (List<ShoppingCartElements>) session.getAttribute("cartItems");
        Integer total = calculateAll(cartItems);
        request.setAttribute("total", total);
    }

    @Override
    public Integer calculateAll(List<ShoppingCartElements> cartItems) {

        int total = 0;
        for (ShoppingCartElements cartItem : cartItems) {
            total += cartItem.getFoodId().getMenuFoodPrice() * cartItem.getFoodNum();
        }
        return total;
    }

    @Override
    public List<Order> getOrderItem(List<Order> orders) {
        HashMap<LocalDateTime, Order> map = new HashMap<>();
        for (Order order : orders) {
            if (!order.getOrderItem().isEmpty()) {
                order.getOrderItem().clear();
            }
        }

        for (Order order : orders) {
            Menu menu = menuMapper.getMenuById(order.getMenuFoodId().getMenuFoodId());
            ShoppingCartElements elements = new ShoppingCartElements(menu, order.getOrderFoodNum());
            order.setRest(restaurantMapper.selectById(order.getRestId()));
            if (map.containsKey(order.getOrderDateTime())) {
                Order tempOrder = map.get(order.getOrderDateTime());
                tempOrder.getOrderItem().add(elements);
                tempOrder.setOrderTotal(tempOrder.getOrderTotal() + order.getOrderTotalPrice());
                map.put(order.getOrderDateTime(), tempOrder);
            } else {
                order.getOrderItem().add(elements);
                order.setOrderTotal(order.getOrderTotalPrice());
                map.put(order.getOrderDateTime(), order);
            }
        }


        ArrayList<LocalDateTime> ordersSort = new ArrayList<>(map.keySet());
        Collections.sort(ordersSort, new Comparator<LocalDateTime>() {
            @Override
            public int compare(LocalDateTime o1, LocalDateTime o2) {
                return o2.compareTo(o1);
            }
        });

        ArrayList<Order> tempOrder = new ArrayList<>();
        for (LocalDateTime time : ordersSort) {
            Order order = map.get(time);
            tempOrder.add(order);
        }


        return tempOrder;
    }

    @Override
    public void getCommentOrder(Member member, HttpServletRequest request, HttpSession session) {

        List<Order> orders = orderMapper.getOrdersByEmailAndRestAccepted(member.getMemEmail(), 3);
        List<Order> orderItem = getOrderItem(orders);
        List<Order> commentOrder = new ArrayList<>();
        for (Order order : orderItem) {
            LambdaQueryWrapper<Ratings> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Ratings::getRatingsOrderId, order.getOrderId());
            Ratings ratings1 = ratingsMapper.selectOne(queryWrapper);
            Ratings ratings = ratingsMapper.selectOne(queryWrapper);
            if (ratings == null) {
                commentOrder.add(order);
            }
        }
        if (commentOrder.size() != 0) {
            session.setAttribute("currentCommentOrder", commentOrder);
        } else {
            session.removeAttribute("currentCommentOrder");
        }
    }
}
