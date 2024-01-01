package com.my.controller;

import com.my.pojo.Menu;
import com.my.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MenuController {

    @Autowired
    private MenuService menuService;

    public MenuController() {
    }

    @RequestMapping("/lookMenu")
    public String lookMenu(Integer restId, HttpSession session) {
        //查看所有菜單，並根據類別進行分類
        menuService.lookMenu(restId,session);
        return "menu";
    }
}
