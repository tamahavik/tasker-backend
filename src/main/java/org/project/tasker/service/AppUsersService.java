package org.project.tasker.service;

import org.project.tasker.model.dto.AppUsersRequest;
import org.project.tasker.model.dto.AppUsersResponse;
import org.project.tasker.model.dto.PagingRequest;

import java.util.List;

public interface AppUsersService {
    List<AppUsersResponse> findAllUser(PagingRequest page);

    AppUsersResponse findUserById(String id);

    AppUsersResponse createUser(AppUsersRequest request);

    AppUsersResponse updateUser(String id, AppUsersRequest request);

    String deleteUser(String id);

}
