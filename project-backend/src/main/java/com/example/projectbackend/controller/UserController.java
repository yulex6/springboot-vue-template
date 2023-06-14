package com.example.projectbackend.controller;

import com.example.projectbackend.entity.RestBean;
import com.example.projectbackend.entity.user.AccountUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * @author 87452
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @GetMapping("/me")
    public RestBean<AccountUser> getUser(@SessionAttribute("account") AccountUser user){
        return RestBean.success(user);
    }
}
