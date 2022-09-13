package org.hatama.tasker.service;

import org.hatama.tasker.model.dto.AppUsersRequest;
import org.hatama.tasker.model.dto.AppUsersResponse;
import org.hatama.tasker.model.dto.PagingRequest;

import java.util.List;

public interface AppUsersService {
    List<AppUsersResponse> findAllUser(PagingRequest page);

    AppUsersResponse findUserById(String id);

    AppUsersResponse createUser(AppUsersRequest request);

    AppUsersResponse updateUser(String id, AppUsersRequest request);

    String deleteUser(String id);

}
