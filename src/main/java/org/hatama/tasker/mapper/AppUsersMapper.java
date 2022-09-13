package org.hatama.tasker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.hatama.tasker.model.dto.AppUsersRequest;
import org.hatama.tasker.model.dto.AppUsersResponse;
import org.hatama.tasker.model.dto.RegisterRequest;
import org.hatama.tasker.model.entity.AppUsers;

@Mapper(componentModel = "spring")
public interface AppUsersMapper extends BaseMapper<AppUsers, AppUsersRequest, AppUsersResponse> {
    AppUsersMapper INSTANCE = Mappers.getMapper(AppUsersMapper.class);

    AppUsers registerToAppUsers(RegisterRequest request);
}
