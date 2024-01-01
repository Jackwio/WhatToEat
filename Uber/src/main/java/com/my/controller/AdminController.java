package com.my.controller;

import com.my.pojo.Admin;
import com.my.service.AdminService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.PrintWriter;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    public AdminController() {
    }

    @ResponseBody
    @RequestMapping("/loginAdmin")
    public String loginAdmin(String adminEmail, String password, HttpSession session) {
        Admin tempAdmin = adminService.loginAdmin(adminEmail, password, session);
        if (tempAdmin != null) {
            return "管理員登入成功";
        }
        return "管理員登入失敗";
    }

    @ResponseBody
    @RequestMapping("/registerAdmin")
    public String adminRegister(String adminEmail, String password, HttpSession session, HttpServletResponse response) {
        Admin tempAdmin = adminService.registerAdmin(adminEmail, session, password);
        //若唯一，則儲存，並回到index頁面
        if (tempAdmin == null) {
            return "新增成功";
        }
        return "新增失敗";
    }

    @RequestMapping("/logoutAdmin")
    public String logoutAdmin(HttpSession session) {
        session.removeAttribute("admin");
        return "index";
    }

    @RequestMapping("/checkAllMember")
    public String checkAllMember(HttpSession session) {
        adminService.checkAllMember(session);
        return "admin/memberList";
    }

    @RequestMapping("/checkAllOrder")
    public String checkAllOrder(HttpSession session) {
        adminService.checkAllOrder(session);
        return "admin/historyOrder";
    }

    @RequestMapping("/checkAllRestaurant")
    public String checkAllRestaurant(HttpSession session) {
        adminService.checkAllRestaurant(session);
        return "admin/restaurantList";
    }

    @ResponseBody
    @RequestMapping("/stopRest")
    public String stopRest(Integer restId){
        Integer affect = adminService.deleteRestaurant(restId);
        if(affect==0){
            return "解鎖成功";
        }else{
            return "封鎖成功";
        }
    }

    @RequestMapping("/deleteMember")
    public String deleteMember(String memEmail){
        adminService.deleteMember(memEmail);
        return "redirect:/checkAllMember";
    }

    @RequestMapping("/editOrder")
    public String editOrder(Long orderId,String state){
        adminService.editOrder(orderId,state);
        return "redirect:/checkAllOrder";
    }

}
