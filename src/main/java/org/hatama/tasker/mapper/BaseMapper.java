package org.hatama.tasker.mapper;

import java.util.List;

public interface BaseMapper<Dom, Req, Res> {
    Res toResponse(Dom user);

    Dom toDomain(Req request);

    List<Res> toListResponse(List<Dom> users);

    List<Dom> toListDomain(List<Req> usersRequests);
}
