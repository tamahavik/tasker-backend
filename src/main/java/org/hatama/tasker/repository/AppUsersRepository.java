package org.hatama.tasker.repository;

import org.hatama.tasker.model.entity.AppUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUsersRepository extends JpaRepository<AppUsers, String> {
    Optional<AppUsers> findByUsername(String username);

    Optional<AppUsers> findByEmail(String email);

}
