package com.my.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.mapper.FavoriteMapper;
import com.my.mapper.RestaurantMapper;
import com.my.pojo.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.event.MouseWheelEvent;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl extends ServiceImpl<RestaurantMapper, Restaurant> implements RestaurantService {

    @Autowired
    private RestaurantMapper restaurantMapper;
    @Autowired
    MemberService memberService;

    @Override
    public void lookRestaurant(HttpSession session, Integer pageNumber, String keyword, String action, String position, Integer time, String menuType, String foodClass, Integer menuMoney, String menuConstraint) {

        Member currentUser = new Member();
        if (session.getAttribute("currentUser") == null) {
            Member.tempUser = new Member();
            ShoppingCart.tempCart = new HashMap<>();
            ArrayList<ShoppingCartElements> cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        } else {
            currentUser = (Member) session.getAttribute("currentUser");
        }
        List<Restaurant> restaurants;
        restaurants = getAllRestaurant();

        if (pageNumber == null) {
            pageNumber = 1;
        }
        //判斷是搜尋還是瀏覽
        if ("search".equals(action)) {
            pageNumber = 1;
            if (keyword.equals("")) {
                session.removeAttribute("menuType");
                session.removeAttribute("menuMoney");
                session.removeAttribute("foodClass");
                session.removeAttribute("menuConstraint");
            }
        }

        //配置分頁
        Page<Restaurant> page = new Page<>(pageNumber, 4);

//        位置篩選(完)
        if (position != null) {
            session.setAttribute("position", position);
        } else {
            position = (String) session.getAttribute("position");
        }
        restaurants = selectRestaurantByPosition(restaurants, position);

//        if(time!=null){
//            if(time==-1){
//                LocalDateTime now = LocalDateTime.now();
//                time = now.getHour();
//                session.setAttribute("time", time);
//            }
//            session.setAttribute("time", time);
//        }else{
//            time = (Integer) session.getAttribute("time");
//        }

//        restaurants = selectRestaurantByTime(restaurants, time);


        //關鍵字篩選
        String tempKeyword = (String) session.getAttribute("keyword");
        if (keyword != null || tempKeyword != null) {
            if (keyword != null) {
                session.setAttribute("keyword", keyword);
            } else {
                keyword = (String) session.getAttribute("keyword");
            }
            restaurants = selectRestaurantByKeyword(restaurants, keyword);
        }

        //篩選食物種類
        String tempMenuType = (String) session.getAttribute("menuType");
        if (menuType != null) {
            restaurants = selectRestaurantByMenuType(restaurants, menuType);
            session.setAttribute("menuType", menuType);
        } else if (tempMenuType != null) {
            restaurants = selectRestaurantByMenuType(restaurants, tempMenuType);
        }

        //篩選價錢(完)
        Integer tempMenuMoney = null;
        if (menuMoney != null && menuMoney == 0) {
            session.removeAttribute("menuMoney");
        } else {
            if (session.getAttribute("menuMoney") != null) {
                tempMenuMoney = (Integer) session.getAttribute("menuMoney");
            }
            if (menuMoney != null) {
                restaurants = selectRestaurantByMenuMoney(restaurants, menuMoney);
                session.setAttribute("menuMoney", menuMoney);
            } else if (tempMenuMoney != null) {
                restaurants = selectRestaurantByMenuMoney(restaurants, tempMenuMoney);
            }
        }


        //篩選食物限制(完)
        if (("").equals(menuConstraint)) {
            session.removeAttribute("menuConstraint");
        } else {
            String tempMenuConstraint = null;
            if (session.getAttribute("menuConstraint") != null) {
                tempMenuConstraint = (String) session.getAttribute("menuConstraint");
            }
            if (menuConstraint != null) {
                restaurants = selectRestaurantByMenuConstraint(restaurants, menuConstraint);
                session.setAttribute("menuConstraint", menuConstraint);
            } else if (tempMenuConstraint != null) {
                restaurants = selectRestaurantByMenuConstraint(restaurants, tempMenuConstraint);
            }
        }


        Long allRestaurants = 0L;
        //查詢所有餐廳數
        if (restaurants.size() != 0) {
            allRestaurants = getRestaurantNumber(session, restaurants);

//            if(("").equals(foodClass)){
//                session.removeAttribute("foodClass");
//            }

            String tempFoodClass = (String) session.getAttribute("foodClass");
            if (("").equals(foodClass)) {
                restaurants = getRestaurant(page, restaurants);
            } else if (foodClass != null || tempFoodClass != null) {
                if (tempFoodClass == null) {
                    session.setAttribute("foodClass", foodClass);
                }
                String restIds = "";
                for (int i = 0; i < restaurants.size(); i++) {
                    restIds += restaurants.get(i).getRestId();
                    if (restaurants.size() != i + 1) {
                        restIds += ",";
                    }
                }
                restaurants = restaurantMapper.getRestaurant((pageNumber - 1) * 4, restIds);
            } else {
                //查看分頁所在的餐廳
                restaurants = getRestaurant(page, restaurants);
            }
        }
        memberService.getFavorite(currentUser.getMemEmail(), session);

        //保存在session域中
        session.setAttribute("restaurants", restaurants);
        session.setAttribute("pageNumber", pageNumber);
        session.setAttribute("maxPageNumber", (allRestaurants + 3) / 4);
    }

    @Override
    public Long getRestaurantNumber(HttpSession session, List<Restaurant> restaurants) {
        LambdaQueryWrapper<Restaurant> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Restaurant::getRestId, restaurants.stream().map(Restaurant::getRestId).collect(Collectors.toList()));
        return count(wrapper);
    }

    @Override
    public List<Restaurant> selectRestaurantByKeyword(List<Restaurant> restaurants, String keyword) {
        return restaurants.stream().filter(restaurant -> restaurant.getRestName().contains(keyword)).collect(Collectors.toList());
    }

    @Override
    public List<Restaurant> selectRestaurantByTime(List<Restaurant> restaurants, Integer time) {
        return restaurants.stream().filter(restaurant -> time >= restaurant.getRestOpenTime() && time <= restaurant.getRestCloseTime()).collect(Collectors.toList());
    }

    @Override
    public List<Restaurant> selectRestaurantByPosition(List<Restaurant> restaurants, String position) {
        if (("").equals(position)) {
            return restaurantMapper.getAllRestaurant();
        }
        return restaurants.stream().filter(restaurant -> restaurant.getRestLocation().equals(position)).collect(Collectors.toList());
    }

    @Override
    public List<Restaurant> selectRestaurantByMenuType(List<Restaurant> restaurants, String menuType) {
        return restaurants.stream().filter(restaurant -> restaurant.getRestFoodType().equals(menuType)).collect(Collectors.toList());
    }

    @Override
    public List<Restaurant> selectRestaurantByMenuMoney(List<Restaurant> restaurants, Integer menuMoney) {
        return restaurants.stream().filter(restaurant -> restaurant.getRestPrice() == menuMoney).collect(Collectors.toList());
    }

    @Override
    public List<Restaurant> selectRestaurantByMenuConstraint(List<Restaurant> restaurants, String menuConstraint) {
        return restaurants.stream().filter(restaurant -> restaurant.getRestDietConstraint().equals(menuConstraint)).collect(Collectors.toList());
    }

    @Override
    public List<Restaurant> getRestaurant(Page<Restaurant> page, List<Restaurant> restaurants) {
        LambdaQueryWrapper<Restaurant> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Restaurant::getRestId, restaurants.stream().map(Restaurant::getRestId).collect(Collectors.toList()));
        return restaurantMapper.selectPage(page, wrapper).getRecords();
    }

    @Override
    public List<Restaurant> getAllRestaurant() {
        List<Restaurant> restaurants = restaurantMapper.selectList(null);
        return restaurants;
    }

    @Override
    public void useTurntable(HttpSession session) {
        String position = (String) session.getAttribute("position");
        LambdaQueryWrapper<Restaurant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Restaurant::getRestLocation,position);
        long restNumbers = count(wrapper);

        ArrayList<Integer> restId = new ArrayList<>();
        ArrayList<Restaurant> turntableRests = new ArrayList<>();
        while (restId.size() <= 8) {
            int randomNumber = (int) (Math.random() * Math.toIntExact(restNumbers)) + 1;
            while (restId.contains(randomNumber)) {
                randomNumber = (int) (Math.random() * Math.toIntExact(restNumbers)) + 1;
            }
            Restaurant restaurant = restaurantMapper.selectById(randomNumber);
            if (restaurant != null) {
                restId.add(randomNumber);
                turntableRests.add(restaurant);
            }
        }
        session.setAttribute("turntableRests", turntableRests);
    }

    @Override
    public Integer getTurnTableResult(Integer target, HttpSession session) {
        List<Restaurant> turntableRests = (List<Restaurant>) session.getAttribute("turntableRests");
        Restaurant restaurant = turntableRests.get(target);
        return restaurant.getRestId();
    }
}
