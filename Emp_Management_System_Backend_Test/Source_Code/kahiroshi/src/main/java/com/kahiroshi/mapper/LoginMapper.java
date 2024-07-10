package com.kahiroshi.mapper;

import com.kahiroshi.object.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginMapper {

    @Select("select id, username from user where username = #{username} and password = #{password}")
    User findUserByUsernameAndPassword(User userData);
}
