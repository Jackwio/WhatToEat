package com.my.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.my.pojo.Menu;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MenuService extends IService<Menu> {
    void lookMenu(Integer restId, HttpSession session);

    List<String> getTypes(Integer restId,HttpSession session);

    void getMenus(HttpSession session);

    Integer addMenu(MultipartFile multipartFile, String menuFoodName, Integer menuFoodPrice, String menuFoodType,HttpSession session);

    Integer editMenu(MultipartFile multipartFile, String menuFoodName, Integer menuFoodPrice, String menuFoodType,Integer menuFoodId, HttpSession session);
}
