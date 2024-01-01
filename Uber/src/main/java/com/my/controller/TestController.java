package com.my.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class TestController {

    @ResponseBody
    @RequestMapping("/test")
    public String doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
        /** 取出經解析到JSONObject之Request參數 */
        //String movie_name = jso.getString("movieName");
        //int movie_time = jso.getInt("duration");
        //String movie_description = jso.getString("description");
        //String movie_image = jso.getString("movie_image");
        // 設置回應的字符編碼為UTF-8
        response.setCharacterEncoding("UTF-8");

        // 設置回應的內容類型為JSON，並使用UTF-8編碼
        response.setContentType("application/json;charset=UTF-8");
        String movie_name = request.getParameter("movieName");
        //int movie_type = jso.getInt("category");
        System.out.println(movie_name);
        String successMessage = "成功！";

        // 設置回應的內容類型為JSON
        response.setContentType("application/json");

        // 獲取PrintWriter對象，用於將JSON數據寫入OutputStream
        PrintWriter out = response.getWriter();

        // 構建成功的JSON回應
        String jsonResponse = "{\"success\": true, \"message\": \"" + successMessage + "\"}";

        // 寫入JSON數據到OutputStream
        out.print(jsonResponse);

        // 關閉PrintWriter
        out.flush();
        out.close();
return "e";
    }
}
