package com.my.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ratings {
    private String memEmail;
    private Integer ratingsStar;
    private String ratingsContent;
    private Long ratingsOrderId;

    @TableField(exist = false)
    private Order order;

    public Ratings(String memEmail, Integer ratingsStar, String ratingsContent, Long ratingsOrderId) {
        this.memEmail = memEmail;
        this.ratingsStar = ratingsStar;
        this.ratingsContent = ratingsContent;
        this.ratingsOrderId = ratingsOrderId;
    }
}
