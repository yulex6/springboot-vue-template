package com.example.projectbackend.mapper;

import com.example.projectbackend.entity.auth.Account;
import com.example.projectbackend.entity.user.AccountUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author 87452
 */
@Mapper
public interface UserMapper {

    @Select("select * from account where username = #{text} or email = #{text}")
    Account findAccountByUsernameOrEmail(String text);
    @Select("select * from account where username = #{text} or email = #{text}")
    AccountUser findAccountUserByUsernameOrEmail(String text);
    @Insert("insert into account (username, password, email) VALUES (#{username}, #{password}, #{email})")
    int createAccount(String username, String password, String email);

    @Update("update account set password = #{password} where email = #{email}")
    int resetPasswordByEmail(String password, String email);
}
