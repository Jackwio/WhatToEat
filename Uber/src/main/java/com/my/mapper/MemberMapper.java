package com.my.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.pojo.Member;

public interface MemberMapper extends BaseMapper<Member> {
    void addMember(Member member);

    Member getMemberByMemEmail(String memEmail);
}
