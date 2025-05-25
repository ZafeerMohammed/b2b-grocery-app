package com.b2bapp.grocery.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponse {

    private String token;

    private String email;

    private String role;

}
