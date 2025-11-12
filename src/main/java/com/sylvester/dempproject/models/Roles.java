package com.sylvester.dempproject.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "roles")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "role_type")
    private Role roleType;

    @OneToMany(mappedBy = "roles", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JsonBackReference
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    public Roles(Role role) {
        this.roleType = role;

    }
}
