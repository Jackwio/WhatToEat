package com.my.Enum;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum OrderState {
    PAID(0,"已付款"),FINISH(1,"已完成"),REJECTEDBYREST(2,"被拒絕");

    @EnumValue
    private int code;
    private String value;

    OrderState(int code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
