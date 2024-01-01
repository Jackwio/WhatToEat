package com.my.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.my.mapper.FavoriteMapper;
import com.my.mapper.MemPasswordMapper;
import com.my.pojo.Favorite;
import com.my.pojo.Member;
import com.my.pojo.Restaurant;
import com.my.service.MemberService;
import com.my.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    public MemberController() {
    }

    @ResponseBody
    @RequestMapping("/login")
    public String login(String memEmail, String password, HttpSession session,HttpServletRequest request) {

        Member tempMember = memberService.loginMember(memEmail, password, session,request);
        if (tempMember == null) {
            return "帳號或密碼錯誤";
        }
        if (tempMember.getMemType() == 1) {
            return "餐廳業者登入成功";
        }
        return "會員登入成功";

    }

    @ResponseBody
    @RequestMapping("/register")
    public String register(@RequestParam("memPhoto") MultipartFile multipartFile, String memEmail,
                           String memName, String memPhoneNum, String password, HttpSession session, HttpServletRequest request) {
        List<Member> tempMember = memberService.registerMember(multipartFile, new Member(memEmail, memName, memPhoneNum), password, session);
//        若唯一，則儲存，並回到index頁面
        if (tempMember.size() == 0) {
            return "註冊成功";
        } else {
            return "email或電話重複";
        }
    }

    @ResponseBody
    @RequestMapping("/editMemberInfo")
    public String editMemberInfo(Member member, String password, HttpSession session, HttpServletRequest request) {
        Integer affect = memberService.editMemberInfo(member, password, session, request);
        if (affect == 0) {
            return "電話號碼重複修改失敗";
        } else {
            return "變動成功";
        }
    }

    @RequestMapping("/logOut")
    public String logOut(HttpSession session) {
        //取消綁定當前使用者
        session.removeAttribute("position");
        session.removeAttribute("time");
        session.removeAttribute("menuType");
        session.removeAttribute("menuMoney");
        session.removeAttribute("foodClass");
        session.removeAttribute("menuConstraint");
        session.removeAttribute("currentUser");
        session.removeAttribute("keyword");
        session.removeAttribute("restaurants");
        session.removeAttribute("pageNumber");
        session.removeAttribute("maxPageNumber");
        //回到index頁面
        return "index";
    }

    @RequestMapping("/collectRest")
    public String collectRest(Integer restId, HttpSession session, String pageNumber) {
        memberService.collectRest(restId, session, pageNumber);
        if (pageNumber == null) {
            return "redirect:/showCollect";
        }
        return "redirect:/lookRestaurant?pageNumber=" + pageNumber;
    }

    @RequestMapping("/cancelCollectRest")
    public String cancelCollectRest(Integer restId, HttpSession session, String pageNumber) {
        Member member = (Member) session.getAttribute("currentUser");
        memberService.cancelCollectRest(restId, session, pageNumber);
        if (pageNumber == null) {
            return "redirect:/showCollect";
        }
        return "redirect:/lookRestaurant?pageNumber=" + pageNumber;
    }

    @RequestMapping("/showCollect")
    public String showCollect(HttpSession session) {
        Member currentUser = (Member) session.getAttribute("currentUser");
        memberService.getFavorite(currentUser.getMemEmail(),session);
        List<Restaurant> restaurants = memberService.getAllFavoriteRest((List<Integer>) session.getAttribute("favoriteRestIds"));
        session.setAttribute("restaurants", restaurants);
        return "collect";
    }

    @RequestMapping("/goToProfile")
    public String goToProfile(HttpServletRequest request, HttpSession session) {
        memberService.goToProfile(request, session);
        return "profile";
    }

    @RequestMapping("/getMemberOrder")
    public String getOrder(HttpSession session, HttpServletRequest request) {
        memberService.getOrder(session, request);
        return "forward:/goToLookOrder";
    }

    @RequestMapping("/goToComment")
    public String goToComment(Long orderId,String comment,Integer star,HttpSession session){
        memberService.goToComment(orderId,comment,star,session);
        return "redirect:/lookRestaurant";
    }

    @RequestMapping("/cancelComment")
    public String cancelComment(Long orderId,HttpSession session){
        memberService.cancelComment(orderId,session);
        return "redirect:/lookRestaurant";
    }
}
