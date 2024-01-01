package com.my.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.pojo.Restaurant;

import java.util.List;

public interface RestaurantMapper extends BaseMapper<Restaurant> {
    List<Restaurant> getRestaurant(Integer pageNumber, String restIds);

    List<Restaurant> getAllRestaurant();

    void updateRest(Integer restId);
}
