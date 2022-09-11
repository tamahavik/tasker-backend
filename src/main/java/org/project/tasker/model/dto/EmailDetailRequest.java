package org.project.tasker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EmailDetailRequest {
    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;
}
