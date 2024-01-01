package com.my.controller;

import com.my.pojo.Member;
import com.my.pojo.Order;
import com.my.pojo.Restaurant;
import com.my.service.MenuService;
import com.my.service.RestaurantBackService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@Controller
public class RestaurantBackController {
    @Autowired
    private RestaurantBackService restaurantBackService;
    @Autowired
    private MenuService menuService;

    public RestaurantBackController() {
    }

    @ResponseBody
    @RequestMapping("/getOrder")
    public List<Order> getOrder(HttpSession session, Integer restId) {
        List<Order> order = restaurantBackService.getOrder(session, restId);
        return order;
    }

    //拒絕會員訂單
    @RequestMapping("/rejectOrder")
    public String rejectOrder(HttpServletRequest request, HttpSession session, Long orderId) {
        restaurantBackService.rejectOrder(request, session, orderId);
        //回到main
        return "forward:/getOrderReceived";
    }

    //取得未接單的訂單
    @RequestMapping("/getOrderNotReceive")
    public String getOrderNotReceive(HttpServletRequest request, HttpSession session) {
        restaurantBackService.getOrderByState(request, session, 0);
        return "restBack/receiveOrder";
    }

    //接收會員訂單
    @RequestMapping("/acceptOrder")
    public String acceptOrder(HttpServletRequest request, HttpSession session, Long orderId) {
        restaurantBackService.acceptOrder(request, session, orderId);
        //回到main
        return "forward:/getOrderReceived";
    }

    @RequestMapping("/getOrderReceived")
    public String getOrderReceived(HttpServletRequest request, HttpSession session) {
        restaurantBackService.getOrderByState(request, session, 2);
        //回到main
        return "restBack/receiveOrder";
    }

    //取得接受的訂單
//    @RequestMapping("/getAcceptedOrder")
//    public String getAcceptedOrder(HttpServletRequest request, HttpSession session) {
//        restaurantBackService.getOrderByState(request, session, 2);
//        return "restBack/receiveOrder";
//    }

    //取得訂單，以便進行訂單管理
    @RequestMapping("/getAllOrder")
    public String getAllOrder(HttpServletRequest request, HttpSession session) {
        restaurantBackService.getOrderByState(request, session, null);
        return "restBack/orderManage";
    }

    @RequestMapping("/editRestaurant")
    public String editRestaurant(HttpSession session, Restaurant restaurant) {
        restaurantBackService.editRestaurant(restaurant, session);
        //回到main
        return "main";
    }

    @ResponseBody
    @RequestMapping("/registerOwner")
    public String registerOwner(@RequestParam("memPhoto") MultipartFile multipartFileMem, String memEmail,
                                String memPhoneNum, String password
            , @RequestParam("restPhoto") MultipartFile multipartFileRest, String restName, String restLocation,
                                Integer restPrice, Integer restOpenTime, Integer restCloseTime, String restFoodType, String restDietConstraint, HttpServletRequest request) {

        Member member = new Member();
        member.setMemPhoneNum(memPhoneNum);
        member.setMemEmail(memEmail);
        Restaurant restaurant = new Restaurant(restName, restLocation, restPrice, restOpenTime, restCloseTime, restFoodType, restDietConstraint, 0);
        try {
            restaurantBackService.register(multipartFileMem, member, password, multipartFileRest, restaurant, request);
        } catch (Exception e) {
            return "註冊失敗";
        }
        //若唯一，則儲存，並回到index頁面
        return "註冊成功";

    }

    @RequestMapping("/loginOwner")
    public String loginOwner(String ownerEmail, String password, HttpSession session) {
        Restaurant tempRestaurant = restaurantBackService.loginOwner(ownerEmail, password, session);
        if (tempRestaurant != null) {
            return "restIndex";
        }
        return "login";
    }

    @ResponseBody
    @RequestMapping(value = "/changePhoto")
    public String changePhoto(@RequestPart(value = "image", required = false) MultipartFile multipartFile, @RequestParam(value = "restName", required = false) String restName, HttpSession session) {
        Integer affect = restaurantBackService.changePhoto(multipartFile, session);
        if (restName != null) {
            affect = restaurantBackService.changeName(restName, session);
        }
        if (affect == 0) {
            return "無產生變動";
        } else {
            return "變動成功";
        }
    }

    @RequestMapping("/finishOrder")
    public String finishOrder(Integer orderId, HttpServletRequest request, HttpSession session) {
        restaurantBackService.finishOrder(orderId, request, session);
        return "forward:/getOrderReceived";
    }

    @RequestMapping("/goToMenuOfRest")
    public String goToMenuOfRest(HttpSession session) {
        menuService.getMenus(session);
        return "restBack/menu";
    }

    @ResponseBody
    @RequestMapping("/editMenu")
    public String editMenu(@RequestPart(value = "menuFoodPhoto", required = false) MultipartFile multipartFile, String menuFoodName,
                           Integer menuFoodPrice, String menuFoodType, Integer menuFoodId, HttpSession session) {
        Integer i = menuService.editMenu(multipartFile, menuFoodName, menuFoodPrice, menuFoodType, menuFoodId, session);
        if (i == 1) {
            return "餐點修改成功";
        }
        return "餐點修改失敗";
    }

    @RequestMapping("/deleteMenu")
    public String deleteMenu(Integer menuId) {
        menuService.removeById(menuId);
        return "redirect:/goToMenuOfRest";
    }

    @ResponseBody
    @RequestMapping("/addMenu")
    public String addMenu(@RequestPart("menuFoodPhoto") MultipartFile multipartFile, String menuFoodName,
                          Integer menuFoodPrice, String menuFoodType, HttpSession session) {
        Integer i = menuService.addMenu(multipartFile, menuFoodName, menuFoodPrice, menuFoodType, session);
        if (i == 1) {
            return "增加餐點成功";
        }
        return "名稱重複，增加餐點失敗";
    }

    @RequestMapping("/getComment")
    public String getComment(HttpSession session) {
        restaurantBackService.getComment(session);
        return "restBack/comment";
    }

    @RequestMapping("/restBackLogOut")
    public String restBackLogOut(HttpSession session) {
        session.removeAttribute("currentUser");
        session.removeAttribute("currentRestaurant");
        return "index";
    }

    @ResponseBody
    @RequestMapping("/getInitialChartData")
    public ResponseEntity<Map<String, Object>> getInitialChartData(HttpSession session){

        Map<String, Object> initialChartData = restaurantBackService.getInitialChartData(session);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(initialChartData, headers, HttpStatus.OK);
    }

//    @RequestMapping("/refresh")
//    public String refresh(HttpSession session){
//        restaurantBackService.getAverRating(null,session);
//        return "restBack/index";
//    }

}

