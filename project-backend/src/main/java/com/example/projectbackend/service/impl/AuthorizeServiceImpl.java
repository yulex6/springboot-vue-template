package com.example.projectbackend.service.impl;


import com.example.projectbackend.entity.auth.Account;
import com.example.projectbackend.mapper.UserMapper;
import com.example.projectbackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author 87452
 */
@Service
public class AuthorizeServiceImpl implements AuthorizeService {
    @Value("${spring.mail.username}")
    String from;

    @Resource
    UserMapper mapper;

    @Resource
    MailSender mailSender;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        Account account = mapper.findAccountByUsernameOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();
    }

    @Override
    public String sendValidateEmail(String email,String sessionId, boolean hasAccount) {
        String key = "email:" + sessionId + ":" + email+ ":" + hasAccount;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            Long expire = Optional.ofNullable(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L) ;
            if (expire > 120){
                return "请求频繁，请稍后再试";
            }
        }
        Account account = mapper.findAccountByUsernameOrEmail(email);
        if (hasAccount && account == null){
            return "没有此邮件地址账户";
        }
        if (!hasAccount &&account != null){
            return "此邮箱已被注册";
        }
        Random random = new Random();
        int code = random.nextInt(899999) + 100000;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("验证邮件");
        message.setText("验证码："+code);
        try{
            mailSender.send(message);

            stringRedisTemplate.opsForValue().set(key,String.valueOf(code),3, TimeUnit.MINUTES);
            return null;
        }catch (MailException e){
         e.printStackTrace();
            return "邮件发送失败";
        }

    }

    @Override
    public String validateAndRegister(String username, String password, String email, String code,String sessionId) {
        String key = "email:" + sessionId + ":" + email + ":false";
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            String s = stringRedisTemplate.opsForValue().get(key);
            if (s == null) {
                return "验证码失效,请重新请求";
            }
            if (s.equals(code)) {
                Account account = mapper.findAccountByUsernameOrEmail(username);
                if (account != null) {
                    return "此用户名已被注册";
                }
                password = passwordEncoder.encode(password);
                stringRedisTemplate.delete(key);
                if (mapper.createAccount(username, password, email) > 0) {
                    return null;
                }else {
                    return "内部错误";
                }
            }else {
                return "验证码错误，请重新输入";
            }
        }else {
            return "请先获取验证码";
        }
    }

    @Override
    public String validateOnly(String email, String code, String sessionId) {
        String key = "email:" + sessionId + ":" + email + ":true";
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            String s = stringRedisTemplate.opsForValue().get(key);
            if (s == null) {
                return "验证码失效,请重新请求";
            }
            if (s.equals(code)) {
                stringRedisTemplate.delete(key);
                return null;
            }else {
                return "验证码错误，请重新输入";
            }
        }else {
            return "请先获取验证码";
        }
    }

    @Override
    public boolean resetPassword(String password, String email) {
        password = passwordEncoder.encode(password);
        return mapper.resetPasswordByEmail(password,email) > 0;
    }
}
