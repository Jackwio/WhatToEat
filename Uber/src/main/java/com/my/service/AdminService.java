package com.my.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.pojo.Admin;
import jakarta.servlet.http.HttpSession;

public interface AdminService extends IService<Admin> {
    Admin loginAdmin(String adminEmail, String password, HttpSession session);

    Admin registerAdmin(String adminEmail, HttpSession session, String password);

    void checkAllMember(HttpSession session);

    void checkAllOrder(HttpSession session);
    void checkAllRestaurant(HttpSession session);
    Integer deleteRestaurant(Integer restId);

    void deleteMember(String memEmail);

    void editOrder(Long orderId,String state);
}
