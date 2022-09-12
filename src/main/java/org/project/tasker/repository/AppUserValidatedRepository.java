package org.project.tasker.repository;

import org.project.tasker.model.entity.AppUsers;
import org.project.tasker.model.entity.AppUsersValidated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserValidatedRepository extends JpaRepository<AppUsersValidated, String> {
    Optional<AppUsersValidated> findByActivationId(String activationId);

    Optional<AppUsersValidated> findByForgotPasswordId(String forgotPasswordId);

    Optional<AppUsersValidated> findByUserId(AppUsers user);
}
