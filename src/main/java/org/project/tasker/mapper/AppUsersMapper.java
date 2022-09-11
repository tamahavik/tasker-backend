package org.project.tasker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.project.tasker.model.dto.AppUsersRequest;
import org.project.tasker.model.dto.AppUsersResponse;
import org.project.tasker.model.dto.RegisterRequest;
import org.project.tasker.model.entity.AppUsers;

@Mapper(componentModel = "spring")
public interface AppUsersMapper extends BaseMapper<AppUsers, AppUsersRequest, AppUsersResponse> {
    AppUsersMapper INSTANCE = Mappers.getMapper(AppUsersMapper.class);

    AppUsers registerToAppUsers(RegisterRequest request);
}
