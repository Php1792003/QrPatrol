package com.example.demo.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String fullName;
    private boolean enabled;
    private String password;
    private Set<Integer> roleIds;
}