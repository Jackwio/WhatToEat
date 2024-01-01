package com.my.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    @TableId(type = IdType.AUTO)
    private Integer menuFoodId;
    private String menuFoodName;
    private Integer menuFoodPrice;
    private String menuFoodPhoto;
    private String menuFoodType;
    private Integer restId;


    public Menu(Integer menuFoodId) {
        this.menuFoodId = menuFoodId;
    }

    public Menu(String menuFoodName, Integer menuFoodPrice, String menuFoodPhoto, String menuFoodType, Integer restId) {
        this.menuFoodName = menuFoodName;
        this.menuFoodPrice = menuFoodPrice;
        this.menuFoodPhoto = menuFoodPhoto;
        this.menuFoodType = menuFoodType;
        this.restId = restId;
    }
}
