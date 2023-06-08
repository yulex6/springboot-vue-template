package com.example.projectbackend.config;

import com.alibaba.fastjson.JSONObject;
import com.example.projectbackend.entity.RestBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import java.io.IOException;

/**
 * @author 87452
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
   //3.1.0写法
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                )
                .logout((logout) -> logout
                        .logoutUrl("/api/auth/logout")
                )
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint(this::onAuthenticationFailure))
                ;
        return http.build();
    }
    //旧版写法
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//         return http.authorizeHttpRequests()
//                 .anyRequest().authenticated()
//                 .and()
//                 .formLogin()
//                 .loginProcessingUrl("/api/auth/login")
//                 .successHandler(this::onAuthenticationSuccess)
//                 .failureHandler(this::onAuthenticationFailure)
//                 .and()
//                 .logout()
//                 .logoutUrl("/api/auth/logout")
//                 .and()
//                 .csrf().disable()
//                 .exceptionHandling()
//                 .authenticationEntryPoint(this::onAuthenticationFailure)
//                 .and()
//                 .build();
//    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功")));
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401,exception.getMessage())));
    }

}
