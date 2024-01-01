package com.my.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.pojo.MemPassword;

public interface MemPasswordMapper extends BaseMapper<MemPassword> {
    void addPassword(MemPassword memPassword);

    MemPassword selectByEmail(String memEmail);
}
