package com.b2bapp.grocery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING) // Stores the enum as a string in the DB
    private Role role; // WHOLESALER or RETAILER or ADMIN


    @OneToMany(mappedBy = "wholesaler", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // prevent infinite loop if accidentally serialized
    private List<Product> products;

    @Column(nullable = false)
    private boolean active = true;


}
