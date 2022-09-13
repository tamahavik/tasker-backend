package org.hatama.tasker.service;

import org.hatama.tasker.model.dto.EmailDetailRequest;

public interface EmailService {
    String sendSimpleMail(EmailDetailRequest details);

    String sendMailWithAttachment(EmailDetailRequest details);
}
