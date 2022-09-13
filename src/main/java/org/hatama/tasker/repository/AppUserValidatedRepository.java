package org.hatama.tasker.repository;

import org.hatama.tasker.model.entity.AppUsersValidated;
import org.hatama.tasker.model.entity.AppUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserValidatedRepository extends JpaRepository<AppUsersValidated, String> {
    Optional<AppUsersValidated> findByActivationId(String activationId);

    Optional<AppUsersValidated> findByForgotPasswordId(String forgotPasswordId);

    Optional<AppUsersValidated> findByUserId(AppUsers user);
}
