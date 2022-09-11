package org.project.tasker.utils;

import org.project.tasker.model.dto.PagingRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PagingUtils {

    public static Pageable page(PagingRequest page) {
        return PageRequest.of(page.getPage(), page.getSize());
    }

}
