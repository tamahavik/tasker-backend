package org.project.tasker.service.impl;

import org.project.tasker.exception.TaskerException;
import org.project.tasker.model.entity.AppUsers;
import org.project.tasker.repository.AppUsersRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static org.project.tasker.enums.UserStatus.ACTIVE;
import static org.project.tasker.enums.UserStatus.INACTIVE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUsersRepository appUsersRepository;

    public UserDetailsServiceImpl(AppUsersRepository appUsersRepository) {
        this.appUsersRepository = appUsersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUsers user = appUsersRepository.findByUsername(username)
                .orElseThrow(() -> new TaskerException("Invalid Credentials", BAD_REQUEST));
        if (user.getStatus().equalsIgnoreCase(INACTIVE.name())) {
            throw new TaskerException("Please active your account from link in email", UNAUTHORIZED);
        }
        if (!user.getStatus().equalsIgnoreCase(ACTIVE.name())) {
            throw new TaskerException("User Not Active", UNAUTHORIZED);
        }
        return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }
}
