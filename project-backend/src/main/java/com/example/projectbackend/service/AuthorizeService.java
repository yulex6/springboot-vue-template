package com.example.projectbackend.service;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author 87452
 */
public interface AuthorizeService extends UserDetailsService {
    boolean sendValidateEmail(String email,String sessionId);
}
