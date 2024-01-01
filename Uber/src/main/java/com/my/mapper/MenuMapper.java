package com.my.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.pojo.Menu;
import org.apache.ibatis.annotations.Param;

import java.awt.*;

public interface MenuMapper extends BaseMapper<Menu> {
    Menu getMenuById(@Param("mId") Integer menu_id);
}
