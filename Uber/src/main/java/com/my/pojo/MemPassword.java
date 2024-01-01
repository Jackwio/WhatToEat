package com.my.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemPassword {
    @TableId(type = IdType.AUTO)
    private Member member;
    private String password;

    public MemPassword(String password) {
        this.password = password;
    }
}
