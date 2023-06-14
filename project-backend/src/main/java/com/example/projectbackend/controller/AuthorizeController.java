package com.example.projectbackend.controller;

import com.example.projectbackend.entity.RestBean;
import com.example.projectbackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
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
    private final String USERNAME_REGEX = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
    @Resource
    AuthorizeService authorizeService;

    @PostMapping("/valid-email-register")
    public RestBean<String> validateEmailRegister(@Pattern(regexp =EMAIL_REGEX) @RequestParam("email") String email, HttpSession httpSession){
        String s = authorizeService.sendValidateEmail(email, httpSession.getId(),false);
        if (s == null){
            return RestBean.success("邮件已发送,请注意查收");
        }else {
            return RestBean.failure(400,s);
        }
    }

    @PostMapping("/valid-email-reset")
    public RestBean<String> validateEmailReset(@Pattern(regexp =EMAIL_REGEX) @RequestParam("email") String email, HttpSession httpSession){
        String s = authorizeService.sendValidateEmail(email, httpSession.getId(),true);
        if (s == null){
            return RestBean.success("邮件已发送,请注意查收");
        }else {
            return RestBean.failure(400,s);
        }
    }

    @PostMapping("/register")
    public RestBean<String> registerUser(@Pattern(regexp = USERNAME_REGEX) @Length(min = 2,max = 8) @RequestParam("username") String username,
                                         @Length(min = 6,max = 16) @RequestParam("password") String password,
                                         @RequestParam("email") String email,
                                         @Length(min = 6,max = 6) @RequestParam("code") String code, HttpSession httpSession){
        String s = authorizeService.validateAndRegister(username, password, email, code, httpSession.getId());
        if (s == null){
            return RestBean.success("注册成功！");
        }else {
            return RestBean.failure(400,s);
        }
    }

    @PostMapping("/start-reset")
    public RestBean<String> startReset(@Pattern(regexp =EMAIL_REGEX) @RequestParam("email") String email,
                                       @Length(min = 6,max = 6) @RequestParam("code") String code, HttpSession httpSession){
        String s = authorizeService.validateOnly(email, code, httpSession.getId());
        if (s == null){
            httpSession.setAttribute("reset-password",email);
            return RestBean.success();
        }else {
            return RestBean.failure(400,s);
        }
    }

    @PostMapping("/do-reset")
    public RestBean<String> resetPassword(@Length(min = 6,max = 16) @RequestParam("password") String password, HttpSession httpSession){
        String email = (String) httpSession.getAttribute("reset-password");
        if (email  == null){
            return RestBean.failure(401,"请先完成邮箱验证");
        }else if (authorizeService.resetPassword(password,email)){
            httpSession.removeAttribute("reset-password");
            return RestBean.success("密码重置成功");
        }else {
            return RestBean.failure(500,"内部错误");
        }
    }
}
