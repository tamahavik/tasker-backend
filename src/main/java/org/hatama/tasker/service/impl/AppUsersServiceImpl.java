package org.hatama.tasker.service.impl;

import org.hatama.tasker.mapper.AppUsersMapper;
import org.hatama.tasker.model.dto.AppUsersRequest;
import org.hatama.tasker.service.AppUsersService;
import org.hatama.tasker.utils.PagingUtils;
import org.hatama.tasker.utils.SecurityUtils;
import org.hatama.tasker.enums.UserStatus;
import org.hatama.tasker.exception.TaskerException;
import org.hatama.tasker.model.dto.AppUsersResponse;
import org.hatama.tasker.model.dto.PagingRequest;
import org.hatama.tasker.model.entity.AppUsers;
import org.hatama.tasker.repository.AppUsersRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class AppUsersServiceImpl implements AppUsersService {

    private final AppUsersRepository appUsersRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUsersServiceImpl(AppUsersRepository appUsersRepository, PasswordEncoder passwordEncoder) {
        this.appUsersRepository = appUsersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<AppUsersResponse> findAllUser(PagingRequest page) {
        Pageable paging = PagingUtils.page(page);
        List<AppUsers> users = appUsersRepository.findAll(paging).getContent();
        return AppUsersMapper.INSTANCE.toListResponse(users);
    }

    @Override
    public AppUsersResponse findUserById(String id) {
        AppUsers user = appUsersRepository.findById(id)
                .orElseThrow(() -> new TaskerException("Data not found", NOT_FOUND));
        return AppUsersMapper.INSTANCE.toResponse(user);
    }

    @Override
    public AppUsersResponse createUser(AppUsersRequest request) {
        AppUsers user = AppUsersMapper.INSTANCE.toDomain(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedBy(SecurityUtils.getPrincipal());
        AppUsers saved = appUsersRepository.save(user);
        return AppUsersMapper.INSTANCE.toResponse(saved);
    }

    @Override
    public AppUsersResponse updateUser(String id, AppUsersRequest request) {
        AppUsers user = appUsersRepository.findById(id)
                .orElseThrow(() -> new TaskerException("Data not found", NOT_FOUND));
        AppUsers update = AppUsersMapper.INSTANCE.toDomain(request);
        update = update.toBuilder()
                .id(id)
                .createdDate(user.getCreatedDate())
                .createdBy(user.getCreatedBy())
                .modifiedBy(SecurityUtils.getPrincipal())
                .build();
        AppUsers saved = appUsersRepository.save(update);
        return AppUsersMapper.INSTANCE.toResponse(saved);
    }

    @Override
    public String deleteUser(String id) {
        AppUsers user = appUsersRepository.findById(id)
                .orElseThrow(() -> new TaskerException("Data not found", NOT_FOUND));
        user = user.toBuilder()
                .status(UserStatus.DELETED.name())
                .build();
        appUsersRepository.save(user);
        return "SUCCESS";
    }
}
