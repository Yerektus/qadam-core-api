package io.yerektus.qadam.coreapi.modules.leads.model.entity;

import io.yerektus.qadam.coreapi.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("leads")
public class Lead extends BaseEntity {

    @Column("first_name")
    private String firstname;

    @Column("last_name")
    private String lastname;

    @Column("email")
    private String email;

    @Column("company")
    private String company;

    @Column("city")
    private String city;

    @Column("organization_type")
    private String organizationType;

    @Column("source")
    private String source;
}
