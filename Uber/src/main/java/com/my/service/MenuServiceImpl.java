package com.my.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.mapper.MenuMapper;
import com.my.mapper.RestaurantMapper;
import com.my.pojo.Menu;
import com.my.pojo.Restaurant;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RestaurantMapper restaurantMapper;

    @Override
    public void lookMenu(Integer restId, HttpSession session) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getRestId, restId);
        Restaurant restaurant = restaurantMapper.selectById(restId);
        session.setAttribute("restaurant", restaurant);
        List<Menu> menus = menuMapper.selectList(queryWrapper);
        session.setAttribute("menus", menus);
        List<String> types = getTypes(restId, session);
        session.setAttribute("types", types);
    }

    @Override
    public List<String> getTypes(Integer restId, HttpSession session) {
        List<Menu> menus = (List<Menu>) session.getAttribute("menus");
        ArrayList<String> types = new ArrayList<>();
        for (Menu menu : menus) {
            if (!types.contains(menu.getMenuFoodType())) {
                types.add(menu.getMenuFoodType());
            }
        }
        return types;
    }

    @Override
    public void getMenus(HttpSession session) {
        Restaurant currentRestaurant = (Restaurant) session.getAttribute("currentRestaurant");
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getRestId, currentRestaurant.getRestId());
        List<Menu> menus = menuMapper.selectList(wrapper);
        session.setAttribute("currentRestaurantOfMenu", menus);
    }

    @Override
    public Integer addMenu(MultipartFile multipartFile, String menuFoodName, Integer menuFoodPrice, String menuFoodType, HttpSession session) {
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
        Restaurant currentRestaurant = (Restaurant) session.getAttribute("currentRestaurant");

        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getMenuFoodName, menuFoodName).eq(Menu::getRestId, currentRestaurant.getRestId());
        Menu menu = menuMapper.selectOne(queryWrapper);
        if (menu == null) {
            menuMapper.insert(new Menu(menuFoodName, menuFoodPrice, uPhotoPath, menuFoodType, currentRestaurant.getRestId()));
            return 1;
        }
        return 0;
    }

    @Override
    public Integer editMenu(MultipartFile multipartFile, String menuFoodName, Integer menuFoodPrice, String menuFoodType, Integer menuFoodId, HttpSession session) {
        String uPhotoPath;
        if (multipartFile == null) {
            Menu menu = menuMapper.getMenuById(menuFoodId);
            uPhotoPath = menu.getMenuFoodPhoto();
        } else {
            String directoryName = System.getProperty("user.dir");
//        拼出路徑並儲存到當前專案的static的images下
            String uPhotoPrefix = "/static/images/";
            uPhotoPath = uPhotoPrefix + multipartFile.getOriginalFilename();
            File file = new File(directoryName + "/Uber/src/main/resources/webapp" + uPhotoPath);
            try {
                multipartFile.transferTo(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Restaurant currentRestaurant = (Restaurant) session.getAttribute("currentRestaurant");

        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getMenuFoodName, menuFoodName);
        Menu menu = menuMapper.selectOne(queryWrapper);
        if (menu == null) {
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Menu::getMenuFoodId, menuFoodId);
            menuMapper.update(new Menu(menuFoodName, menuFoodPrice, uPhotoPath, menuFoodType, currentRestaurant.getRestId()), wrapper);
            return 1;
        } else if (menu.getMenuFoodName().equals(menuFoodName)) {
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Menu::getMenuFoodId, menuFoodId);
            menuMapper.update(new Menu(menuFoodName, menuFoodPrice, uPhotoPath, menuFoodType, currentRestaurant.getRestId()), wrapper);
            return 1;
        }
        return 0;
    }
}
