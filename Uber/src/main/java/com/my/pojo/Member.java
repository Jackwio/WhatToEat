package com.my.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member implements Serializable {

    public static final long serialVersionUID = 42L;

    @TableId(type = IdType.AUTO)
    private String memEmail;
    private String memName;
    private String memPhoneNum;
    private Integer memType;
    private String memPhoto;

    @TableField(exist = false)
    private ShoppingCart cartId;

    @TableField(exist = false)
    public static Member tempUser;

    public Member(String memEmail, String memName, String memPhoneNum) {
        this.memEmail = memEmail;
        this.memName = memName;
        this.memPhoneNum = memPhoneNum;
    }

    public Member(String memName, String memPhoneNum) {
        this.memName = memName;
        this.memPhoneNum = memPhoneNum;
    }
}
