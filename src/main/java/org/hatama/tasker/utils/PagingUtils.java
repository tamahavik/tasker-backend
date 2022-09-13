package org.hatama.tasker.utils;

import org.hatama.tasker.model.dto.PagingRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PagingUtils {

    public static Pageable page(PagingRequest page) {
        return PageRequest.of(page.getPage(), page.getSize());
    }

}
