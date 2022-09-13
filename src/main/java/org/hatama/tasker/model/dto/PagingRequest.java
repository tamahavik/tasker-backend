package org.hatama.tasker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PagingRequest {
    @NotNull(message = "page cant empty")
    @Min(value = 0, message = "page must more than 0")
    private Integer page;
    @NotNull(message = "size cant empty")
    @Min(value = 5, message = "size must more than 5")
    private Integer size;
}
