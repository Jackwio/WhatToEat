package com.my.Config;

import com.my.Interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.awt.*;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/registerOption").setViewName("registerOption");
        registry.addViewController("/loginOption").setViewName("loginOption");
        registry.addViewController("/loginUser").setViewName("login");
        registry.addViewController("/main").setViewName("main");
        registry.addViewController("/registerUser").setViewName("register");
        registry.addViewController("/logOut").setViewName("login");
        registry.addViewController("/editProfileUser").setViewName("editProfile");
        registry.addViewController("/notMember").setViewName("notMember");
        registry.addViewController("/turntable").setViewName("turntable");
        registry.addViewController("/receiveOrder").setViewName("receiveOrder");
        registry.addViewController("/goToLookOrder").setViewName("order");

        registry.addViewController("/goToRegisterRest").setViewName("restBack/registerRest");
        registry.addViewController("/goToRestBack").setViewName("restBack/index");
        registry.addViewController("/goToRest").setViewName("restBack/restaurant");
//        registry.addViewController("/goToMenuOfRest").setViewName("restBack/menu");
        registry.addViewController("/goToOrderManage").setViewName("restBack/orderManage");
        registry.addViewController("/goToReceiveOrder").setViewName("restBack/receiveOrder");

        registry.addViewController("/goLoginAdmin").setViewName("admin/login");
        registry.addViewController("/goToAdmin").setViewName("admin/index");
        registry.addViewController("/goToAddAdmin").setViewName("admin/addAdmin");

        registry.addViewController("/registerRest").setViewName("restBack/registerRest");

        registry.addViewController("/gotest").setViewName("test");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/images/**")
                .addResourceLocations("file:"+System.getProperty("user.dir")
                        +"\\Uber\\src\\main\\resources\\webapp\\static\\images\\");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/goToPay","/logOut","/collectRest","/cancelCollectRest");
    }
}
