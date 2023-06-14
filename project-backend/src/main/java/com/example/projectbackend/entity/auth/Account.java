package com.example.projectbackend.entity.auth;

import lombok.Data;

/**
 * @author 87452
 */
@Data
public class Account {
    int id;
    String email;
    String username;
    String password;
}
