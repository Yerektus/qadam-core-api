package io.yerektus.qadam.coreapi.modules.auth.model.entity;

import io.yerektus.qadam.coreapi.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User extends BaseEntity {

    @Column("first_name")
    private String firstname;

    @Column("last_name")
    private String lastname;

    @Column("phone_number")
    private String phoneNumber;

    @Column("email")
    private String email;

    @Column("password_hash")
    private String passwordHash;

    @Column("role")
    private String role;
}
