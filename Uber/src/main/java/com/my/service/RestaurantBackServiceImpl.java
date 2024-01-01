package com.my.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.Enum.OrderState;
import com.my.Enum.RestOrderState;
import com.my.mapper.*;
import com.my.pojo.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.mockito.internal.matchers.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Math.round;

@Service
public class RestaurantBackServiceImpl extends ServiceImpl<RestaurantMapper, Restaurant> implements RestaurantBackService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RestaurantMapper restaurantMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private MemPasswordMapper memPasswordMapper;
    @Autowired
    private RatingsMapper ratingsMapper;
    @Autowired
    private OrderService orderService;

    //    0未接單
//    1拒絕訂單
//    2接收訂單
//    3完成訂單
    @Override //取得訂單
    public List<Order> getOrder(HttpSession session, Integer restId) {
        //把資料從資料庫取出
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getRestId, restId).eq(Order::getRestAccepted, 2);
        //把資料從資料庫取出加到ArrayList
        List<Order> orders = orderMapper.selectList(queryWrapper);
        if (orders != null) {
            //更新資料
            session.setAttribute("orders", orders);
        }
        return orders;
    }

    @Override //拒絕訂單
    public void rejectOrder(HttpServletRequest request, HttpSession session, Long orderId) {
        //從資料庫取得訂單
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        Order order = orderMapper.selectById(orderId);
        queryWrapper.eq(Order::getOrderDateTime, order.getOrderDateTime());
        //更新訂單狀態
        orderMapper.update(new Order(RestOrderState.REJECT), queryWrapper);
        orderMapper.update(new Order(OrderState.REJECTEDBYREST), queryWrapper);
        request.removeAttribute("orderItems");
    }

    @Override
    public void acceptOrder(HttpServletRequest request, HttpSession session, Long orderId) {
        Integer orderNumbers = (Integer) session.getAttribute("orderNumbers");
        if (orderNumbers == 1) {
            session.removeAttribute("orderNumbers");
        } else {
            session.setAttribute("orderNumbers", orderNumbers - 1);
        }
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        Order order = orderMapper.selectById(orderId);
        queryWrapper.eq(Order::getOrderDateTime, order.getOrderDateTime());
        //更新訂單狀態
        orderMapper.update(new Order(RestOrderState.ACCEPTED), queryWrapper);
        request.removeAttribute("orderItems");
    }

    @Override //編輯餐廳資料
    public void editRestaurant(Restaurant restaurant, HttpSession session) {
        restaurantMapper.updateById(restaurant);
    }

    @Override //註冊餐廳
    public void registerRestaurant(MultipartFile multipartFile, Restaurant restaurant, HttpServletRequest request, String ownerEmail) {
//        獲取當前資料夾路徑
        String directoryName = System.getProperty("user.dir");
//        拼出路徑並儲存到當前專案的static的images下
        String uPhotoPrefix = "/static/images/";
        String uPhotoPath = uPhotoPrefix + multipartFile.getOriginalFilename();
        File file = new File(directoryName + "/Uber/src/main/resources/webapp" + uPhotoPath);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //從資料庫檢查有沒有重複餐廳餐廳(名字)
        LambdaQueryWrapper<Restaurant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Restaurant::getRestName, restaurant.getRestName());
        Restaurant tempRestaurant = restaurantMapper.selectOne(queryWrapper);
        if (tempRestaurant == null) {
            //如果沒有重複新增餐廳到資料庫
            restaurantMapper.insert(new Restaurant(null, restaurant.getRestName(), restaurant.getRestLocation(), restaurant.getRestPrice(),
                    restaurant.getRestOpenTime(), restaurant.getRestCloseTime(), restaurant.getRestFoodType()
                    , restaurant.getRestDietConstraint(), 0.0, uPhotoPath, 0, 0, 0, ownerEmail));
        } else {
            throw new RuntimeException();
        }
    }

    @Override //註冊餐廳擁有者
    public void registerOwner(MultipartFile multipartFile, Member member, Restaurant restaurant, String password, HttpServletRequest request) {

//        獲取當前資料夾路徑
        String directoryName = System.getProperty("user.dir");
//        拼出路徑並儲存到當前專案的static的images下
        String uPhotoPrefix = "/static/images/";
        String uPhotoPath = uPhotoPrefix + multipartFile.getOriginalFilename();
        File file = new File(directoryName + "/Uber/src/main/resources/webapp" + uPhotoPath);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //從資料庫檢查有沒有重複餐廳擁有者(Email OR 電話)
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Member::getMemEmail, member.getMemEmail()).or().eq(Member::getMemPhoneNum, member.getMemPhoneNum());
        List<Member> tempOwner = memberMapper.selectList(queryWrapper);
        if (tempOwner.size() == 0) {
            //如果沒有重複新增餐廳擁有者到資料庫
            memberMapper.addMember(new Member(member.getMemEmail(), member.getMemName(), member.getMemPhoneNum(), 1, uPhotoPath, null));
            memPasswordMapper.addPassword(new MemPassword(member, password));
        } else {
            throw new RuntimeException();
        }
    }

    @Override //餐廳擁有者登入
    public Restaurant loginOwner(String ownerEmail, String password, HttpSession session) {
        //透過email去尋找餐廳擁有者
        Restaurant restaurant = null;
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Member::getMemEmail, ownerEmail);
        Member tempOwner = memberMapper.selectOne(queryWrapper);
        if (tempOwner == null) {
            return null;
        }
        //如果找的到，繼續判斷密碼是否正確，若正確，把當前使用者保存在session裡
        MemPassword memPassword = memPasswordMapper.selectByEmail(ownerEmail);
        if (memPassword.getPassword().equals(password)) {
            session.setAttribute("currentUser", tempOwner);
            //透過用戶id找到餐廳
            LambdaQueryWrapper<Restaurant> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(Restaurant::getOwnerEmail, tempOwner.getMemEmail());
            restaurant = restaurantMapper.selectOne(queryWrapper2);
        }
        return restaurant;
    }

    @Override
    public void getOrderByState(HttpServletRequest request, HttpSession session, Integer state) {
        Restaurant currentRestaurant = (Restaurant) session.getAttribute("currentRestaurant");
        Integer restId = currentRestaurant.getRestId();
        List<Order> orders = null;
        if (state == null) {
            orders = orderMapper.getOrders(restId);
        } else if (state == 2) {
            orders = orderMapper.getOrderByState(restId, 0);
            orders.addAll(orderMapper.getOrderByState(restId, 2));
        } else {
            orders = orderMapper.getOrderByState(restId, state);
        }

        List<Order> orderItems = orderService.getOrderItem(orders);
        if (orderItems != null) {
            request.setAttribute("orderItems", orderItems);
        }
    }

    @Transactional
    @Override
    public void register(MultipartFile multipartFileMem, Member member, String password, MultipartFile multipartFileRest, Restaurant restaurant, HttpServletRequest request) {
        try {
            registerOwner(multipartFileMem, member, restaurant, password, request);
            registerRestaurant(multipartFileRest, restaurant, request, member.getMemEmail());
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Integer changePhoto(MultipartFile multipartFile, HttpSession session) {
        //        獲取當前資料夾路徑
        String directoryName = System.getProperty("user.dir");
//        拼出路徑並儲存到當前專案的static的images下
        String uPhotoPrefix = "/static/images/";
        String uPhotoPath;
        if (multipartFile == null) {
            uPhotoPath = null;
        } else {
            uPhotoPath = uPhotoPrefix + multipartFile.getOriginalFilename();
            File file = new File(directoryName + "/Uber/src/main/resources/webapp" + uPhotoPath);
            try {
                multipartFile.transferTo(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Member currentRestOwner = (Member) session.getAttribute("currentUser");
        Restaurant currentRestaurant = (Restaurant) session.getAttribute("currentRestaurant");

        LambdaQueryWrapper<Restaurant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Restaurant::getOwnerEmail, currentRestOwner.getMemEmail());

        String restPhoto = currentRestaurant.getRestPhoto();
        String stringPhoto = restPhoto.substring(restPhoto.lastIndexOf('/'));
        if (stringPhoto == uPhotoPath || uPhotoPath == null) {
            return 0;
        } else {
            restaurantMapper.update(new Restaurant(uPhotoPath), wrapper);
            currentRestaurant.setRestPhoto(uPhotoPath);
            session.setAttribute("currentRestaurant", currentRestaurant);
            return 1;
        }
    }

    @Override
    public Integer changeName(String restName, HttpSession session) {
        Restaurant currentRestaurant = (Restaurant) session.getAttribute("currentRestaurant");
        Restaurant restaurant = new Restaurant();
        restaurant.setRestId(currentRestaurant.getRestId());
        restaurant.setRestName(restName);
        int update = restaurantMapper.updateById(restaurant);
        Restaurant restaurant1 = restaurantMapper.selectById(currentRestaurant.getRestId());
        session.setAttribute("currentRestaurant", restaurant1);
        return update;
    }

    @Override
    public void finishOrder(Integer orderId, HttpServletRequest request, HttpSession session) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        Order order = orderMapper.selectById(orderId);
        wrapper.eq(Order::getOrderDateTime, order.getOrderDateTime());
        orderMapper.update(new Order(RestOrderState.FINISH), wrapper);
        orderMapper.update(new Order(OrderState.FINISH), wrapper);

        List<Order> orders = orderMapper.getOrderByOrderIdAndRestAccepted(orderId, 3);
        List<Order> orderItem = orderService.getOrderItem(orders);
        request.setAttribute("orderItems", orderItem);
        Restaurant currentRestaurant = (Restaurant) session.getAttribute("currentRestaurant");
        getAverRating(currentRestaurant.getRestId(), session);
    }

    @Override
    public void getComment(HttpSession session) {
        ArrayList<Ratings> ratingsList = new ArrayList<>();
        Restaurant currentRestaurant = (Restaurant) session.getAttribute("currentRestaurant");
        List<Order> orders = orderMapper.getOrders(currentRestaurant.getRestId());
        List<Order> orderItem = orderService.getOrderItem(orders);
        for (Order order : orderItem) {
            LambdaQueryWrapper<Ratings> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Ratings::getRatingsOrderId, order.getOrderId());
            Ratings ratings = ratingsMapper.selectOne(queryWrapper);
            if (ratings != null) {
                ratings.setOrder(order);
                ratingsList.add(ratings);
            }
        }
        session.setAttribute("ratingsList", ratingsList);
    }

    @Override
    public void getAverRating(Integer restId, HttpSession session) {

        List<Order> orders = orderMapper.getOrderByRestState(3);
        List<Order> orderItem = orderService.getOrderItem(orders);
        HashMap<Integer, List<Order>> restaurantListHashMap = new HashMap<>();

        for (Order order : orderItem) {
            if (!restaurantListHashMap.containsKey(order.getRestId())) {
                ArrayList<Order> orders1 = new ArrayList<>();
                orders1.add(order);
                restaurantListHashMap.put(order.getRestId(), orders1);
            } else {
                List<Order> orders1 = restaurantListHashMap.get(order.getRestId());
                orders1.add(order);
                restaurantListHashMap.put(order.getRestId(), orders1);
            }
        }

        for (Map.Entry<Integer, List<Order>> entry : restaurantListHashMap.entrySet()) {
            int orderNum = 0;
            double ratings = 0.0;
            int sales = 0;
            Restaurant restaurant = null;
            for (Order order : entry.getValue()) {
                LambdaQueryWrapper<Ratings> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Ratings::getRatingsOrderId, order.getOrderId());
                Ratings rating = ratingsMapper.selectOne(queryWrapper);
                if (rating != null) {
                    restaurant = restaurantMapper.selectById(entry.getKey());
                    orderNum++;
                    sales += order.getOrderTotal();
                    ratings += rating.getRatingsStar();
                }
            }
            if (restaurant != null) {
                restaurant.setOrdersNum(orderNum);
                String formattedResult = String.format("%.1f", ratings / orderNum);
                restaurant.setRestRatings(Double.parseDouble(formattedResult));
                restaurant.setSales(sales);
                restaurantMapper.updateById(restaurant);
                session.setAttribute("currentRestaurant",restaurant);
            }
        }
        if (restId != null) {
            Restaurant restaurant = restaurantMapper.selectById(restId);
            session.setAttribute("currentRestaurant", restaurant);
        }
    }

    @Override
    public Map<String, Object> getInitialChartData(HttpSession session) {
        int[] orderNumbers = new int[12];
        int[] orderRevenue = new int[12];
        HashMap<String, Object> map = new HashMap<>();

        Restaurant currentRestaurant = (Restaurant) session.getAttribute("currentRestaurant");
        List<Order> orders = orderMapper.getOrderByState(currentRestaurant.getRestId(), 3);
        List<Order> orderItem = orderService.getOrderItem(orders);
        for (Order order : orderItem) {
            int day = order.getOrderDateTime().getDayOfMonth() - 19;
            if (orderNumbers[day] == 0) {
                orderNumbers[day] = 1;
            } else {
                orderNumbers[day] += 1;
            }

            if (orderRevenue[day] == 0) {
                orderRevenue[day] = order.getOrderTotal();
            } else {
                orderRevenue[day] += order.getOrderTotal();
            }

        }
        map.put("orderNumbers", orderNumbers);
        map.put("orderRevenue", orderRevenue);
        return map;
    }
}