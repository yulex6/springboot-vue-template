package com.example.projectbackend.controller;

import com.example.projectbackend.entity.RestBean;
import com.example.projectbackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 87452
 */
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$";
    @Resource
    AuthorizeService authorizeService;

    @PostMapping("/valid-email")
    public RestBean<String> validateEmail(@Pattern(regexp =EMAIL_REGEX) @RequestParam("email") String email, HttpSession httpSession){
        if (authorizeService.sendValidateEmail(email,httpSession.getId())){
            return RestBean.success("邮件已发送,请注意查收");
        }else {
            return RestBean.failure(400,"邮件发送失败");
        }
    }
}
