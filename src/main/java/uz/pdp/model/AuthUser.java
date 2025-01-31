package uz.pdp.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AuthUser {
    private Integer id;

    private String name;

    private int age;

    private String gender;

    private String email;

    private String password;

    private List<AuthRole> roles;
}
