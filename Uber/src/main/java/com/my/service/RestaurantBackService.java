package com.my.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.my.pojo.Member;
import com.my.pojo.Order;
import com.my.pojo.Restaurant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface RestaurantBackService extends IService<Restaurant> {

    void rejectOrder(HttpServletRequest request,HttpSession session, Long orderId);

    List<Order> getOrder(HttpSession session, Integer restId);

    void editRestaurant(Restaurant restaurant, HttpSession session);


    void registerRestaurant(MultipartFile multipartFile, Restaurant restaurant, HttpServletRequest request, String ownerEmail);

    void registerOwner(MultipartFile multipartFile, Member member, Restaurant restaurant, String password, HttpServletRequest request);

    Restaurant loginOwner(String ownerEmail, String password, HttpSession session);

    void getOrderByState(HttpServletRequest request, HttpSession session, Integer state);

    void acceptOrder(HttpServletRequest request,HttpSession session, Long orderId);


    void register(MultipartFile multipartFileMem, Member member, String password, MultipartFile multipartFileRest, Restaurant restaurant, HttpServletRequest request);

    Integer changePhoto(MultipartFile multipartFile, HttpSession session);

    Integer changeName(String restName, HttpSession session);

    void finishOrder(Integer orderId, HttpServletRequest request, HttpSession session);

    void getComment(HttpSession session);
    void getAverRating(Integer restId,HttpSession session);

    Map<String, Object> getInitialChartData(HttpSession session);
}

