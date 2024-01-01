package com.my.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.Enum.OrderState;
import com.my.mapper.*;
import com.my.pojo.*;
import jakarta.servlet.http.HttpSession;
import org.mockito.internal.matchers.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RestaurantMapper restaurantMapper;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private AdminPasswordMapper adminPasswordMapper;

    @Override
    public Admin loginAdmin(String adminEmail, String password, HttpSession session) {
//        把資料從資料庫取出
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getAdminEmail, adminEmail);
        //找到對應的AdminEmail
        Admin tempAdmin = adminMapper.selectOne(queryWrapper);
        if (tempAdmin != null) {
            //判斷密碼是否正確
            LambdaQueryWrapper<AdminPassword> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdminPassword::getAdminEmail,adminEmail);
            AdminPassword adminPassword = adminPasswordMapper.selectOne(wrapper);
            if (adminPassword.getPassword().equals(password)) {
                //更新session資料
                session.setAttribute("admin", tempAdmin);
                return tempAdmin;
            }
            else {
                //密碼錯誤
                return null;
            }
        }
//        找不到對應的AdminEmail
        return null;
    }

    @Override
    public Admin registerAdmin(String adminEmail, HttpSession session, String password) {
//        判斷email是否唯一，不唯一return，並明確顯示email重複
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getAdminEmail, adminEmail);
        Admin tempAdmin = getOne(wrapper);
        if (tempAdmin == null) {
            //儲存資料
            Admin admin = new Admin(adminEmail);
            adminMapper.insert(new Admin(adminEmail));
            //更新session資料
            session.setAttribute("admin", admin);
            //儲存密碼
            AdminPassword adminPassword = new AdminPassword(adminEmail, password);
            adminPasswordMapper.insert(adminPassword);
            ;
        }
        return tempAdmin;
    }

    @Override
    public void checkAllMember(HttpSession session) {
        //把資料從資料庫取出加到ArrayList
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getMemType, 0);
        List<Member> members = memberMapper.selectList(wrapper);
        if (members != null) {
            //更新資料
            session.setAttribute("members", members);
        } else {
            //沒有資料
            session.removeAttribute("members");
        }
    }

    @Override
    public void checkAllOrder(HttpSession session) {
        //把資料從資料庫取出加到ArrayList
        List<Order> orders = orderMapper.geAllOrder();
        List<Order> orderItems = orderService.getOrderItem(orders);
        if (orderItems != null) {
            //更新資料
            session.setAttribute("orderItems", orderItems);
        } else {
            //沒有資料
            session.removeAttribute("orderItems");
        }
    }

    @Override
    public void checkAllRestaurant(HttpSession session) {
        //把資料從資料庫取出加到ArrayList
        List<Restaurant> restaurants = restaurantMapper.getAllRestaurant();
        if (restaurants != null) {
            //更新資料
            session.setAttribute("restaurants", restaurants);
        } else {
            //沒有資料
            session.removeAttribute("restaurants");
        }
    }

    @Override
    public Integer deleteRestaurant(Integer restId) {
        int affect = restaurantMapper.deleteById(restId);
        if (affect == 0) {
            restaurantMapper.updateRest(restId);
        }
        return affect;
    }

    @Override
    public void deleteMember(String memEmail) {
        memberMapper.deleteById(memEmail);
    }

    @Override
    public void editOrder(Long orderId, String state) {

        OrderState orderState = null;
        for (OrderState orderState1 : OrderState.values()) {
            if (orderState1.toString().equals(state)) {
                orderState = orderState1;
                break;
            }
        }

        Order order = orderMapper.selectById(orderId);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderDateTime,order.getOrderDateTime());
        orderMapper.update(new Order(orderState),wrapper);
    }
}
