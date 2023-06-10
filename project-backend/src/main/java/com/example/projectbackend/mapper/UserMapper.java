package com.example.projectbackend.mapper;

import com.example.projectbackend.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author 87452
 */
@Mapper
public interface UserMapper {

    @Select("select * from account where username = #{text} or email = #{text}")
    Account findAccountByUsernameOrEmail(String text);
}
