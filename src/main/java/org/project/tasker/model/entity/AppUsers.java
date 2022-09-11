package org.project.tasker.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.project.tasker.validator.Encrypt;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "mst_user")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class AppUsers extends BaseModel {

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "email", length = 100, nullable = false)
    @Convert(converter = Encrypt.class)
    private String email;

    @Column(name = "username", length = 100, nullable = false)
    private String username;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "phone", length = 100, nullable = false)
    @Convert(converter = Encrypt.class)
    private String phone;

    @Column(name = "status", length = 30, nullable = false)
    private String status;
}
