package org.project.tasker.service;

import org.project.tasker.model.dto.EmailDetailRequest;

public interface EmailService {
    String sendSimpleMail(EmailDetailRequest details);

    String sendMailWithAttachment(EmailDetailRequest details);
}
