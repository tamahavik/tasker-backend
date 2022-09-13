package org.project.tasker.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "trn_user_validated")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AppUsersValidated {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private AppUsers userId;

    @Column(name = "activation_id", length = 64)
    private String activationId;

    @Column(name = "activation_expired")
    private Long activationExpired;

    @Column(name = "forgot_password_id", length = 64)
    private String forgotPasswordId;

    @Column(name = "forgot_password_expired")
    private Long forgotPasswordExpired;
}
