package com.my.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.pojo.Member;
import com.my.pojo.Restaurant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemberService extends IService<Member> {

    List<Member> registerMember(MultipartFile multipartFile, Member member,String password,HttpSession session);

    Member loginMember(String memEmail, String password, HttpSession session,HttpServletRequest request);

    void collectRest(Integer restId, HttpSession session, String pageNumber);
    void cancelCollectRest(Integer restId, HttpSession session, String pageNumber);

    void getFavorite(String memEmail,HttpSession session);

    List<Restaurant> getAllFavoriteRest(List<Integer> favoriteRestIds);

    void goToProfile(HttpServletRequest request, HttpSession session);

    Integer editMemberInfo(Member member, String nPassword, HttpSession session,HttpServletRequest request);

    void getOrder(HttpSession session, HttpServletRequest request);

    void goToComment(Long orderId,String comment,Integer star,HttpSession session);

    void cancelComment(Long orderId, HttpSession session);
}
