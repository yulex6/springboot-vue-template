package com.example.projectbackend.config;

import com.alibaba.fastjson.JSONObject;
import com.example.projectbackend.entity.RestBean;
import com.example.projectbackend.service.AuthorizeService;
import com.example.projectbackend.service.impl.AuthorizeServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @author 87452
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Resource
    AuthorizeService authorizeServiceImpl;

    @Resource
    DataSource dataSource;
   //3.1.0写法
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,PersistentTokenRepository tokenRepository) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                )
                .logout((logout) -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onAuthenticationSuccess)
                )
                .rememberMe((remember) -> remember
                        .rememberMeParameter("remember")
                        .tokenRepository(tokenRepository)
                        .tokenValiditySeconds(3600 * 24 * 7)
                )
                .userDetailsService(authorizeServiceImpl)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint(this::onAuthenticationFailure))
                ;
        return http.build();
    }

    @Bean
     PersistentTokenRepository tokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        jdbcTokenRepository.setCreateTableOnStartup(false);
        return jdbcTokenRepository;
    }
    // corsConfigurationSource名字不能改，竟然是根据名字来的:C
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(Arrays.asList("http://localhost:5173/"));
        cors.setAllowedMethods(Arrays.asList("GET","POST"));
        cors.setAllowCredentials(true);
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        cors.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",cors);
        return source;
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


//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity security) throws Exception {
//        return security.getSharedObject(AuthenticationManagerBuilder.class)
//                .userDetailsService(authorizeService).and().build();
//    }
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setCharacterEncoding("utf-8");
        if (request.getRequestURI().endsWith("/login")) {
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功")));
        } else if (request.getRequestURI().endsWith("/logout")) {
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("退出登录成功")));
        }
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401,exception.getMessage())));
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }
}
