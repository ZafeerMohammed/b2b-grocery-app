package com.b2bapp.grocery.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequest {

    private String email;
    private String password;

}
