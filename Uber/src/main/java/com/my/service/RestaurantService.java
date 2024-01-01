package com.my.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.my.pojo.Restaurant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface RestaurantService extends IService<Restaurant> {
    List<Restaurant> getAllRestaurant();

    List<Restaurant> getRestaurant(Page<Restaurant> page, List<Restaurant> restaurants);

    void lookRestaurant(HttpSession session, Integer pageNumber, String keyword, String action, String position, Integer time, String menuType, String foodClass, Integer menuMoney, String menuConstraint);

    List<Restaurant> selectRestaurantByTime(List<Restaurant> restaurants, Integer time );
    List<Restaurant> selectRestaurantByKeyword(List<Restaurant> restaurants, String keyword);

    List<Restaurant> selectRestaurantByPosition(List<Restaurant> restaurants, String position);
    List<Restaurant> selectRestaurantByMenuType(List<Restaurant> restaurants, String menuType);
    List<Restaurant> selectRestaurantByMenuMoney(List<Restaurant> restaurants, Integer menuMoney);
    List<Restaurant> selectRestaurantByMenuConstraint(List<Restaurant> restaurants, String menuConstraint);

    Long getRestaurantNumber(HttpSession session, List<Restaurant> restaurants);
    void useTurntable(HttpSession session);

    Integer getTurnTableResult(Integer target, HttpSession session);
}
