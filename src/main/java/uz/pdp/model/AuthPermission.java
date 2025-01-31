package uz.pdp.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AuthPermission {
    private Integer id;

    private String name;

    private String code;
}