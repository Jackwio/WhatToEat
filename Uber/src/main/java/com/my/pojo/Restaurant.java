package com.my.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    @TableId(type = IdType.AUTO)
    private Integer restId;
    private String restName;
    private String restLocation;
    private Integer restPrice;
    private Integer restOpenTime;
    private Integer restCloseTime;
    private String restFoodType;
    private String restDietConstraint;
    private Double restRatings;
    private String restPhoto;
    private Integer ordersNum;
    private Integer sales;
    @TableLogic
    private Integer isDeleted;
    private String ownerEmail;

    public Restaurant(String restName, String restLocation, Integer restPrice, Integer restOpenTime, Integer restCloseTime, String restFoodType, String restDietConstraint,Integer ordersNum) {
        this.restName = restName;
        this.restLocation = restLocation;
        this.restPrice = restPrice;
        this.restOpenTime = restOpenTime;
        this.restCloseTime = restCloseTime;
        this.restFoodType = restFoodType;
        this.restDietConstraint = restDietConstraint;
        this.ordersNum=ordersNum;
    }

    public Restaurant(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Restaurant(String restPhoto) {
        this.restPhoto = restPhoto;
    }

    public Restaurant(Integer restId, Double restRatings) {
        this.restId = restId;
        this.restRatings = restRatings;
    }
}
