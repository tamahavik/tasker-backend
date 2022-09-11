package org.project.tasker.service.impl;

import org.project.tasker.exception.TaskerException;
import org.project.tasker.model.dto.EmailDetailRequest;
import org.project.tasker.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Objects;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    String sender;

    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public String sendSimpleMail(EmailDetailRequest details) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());
            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        } catch (Exception e) {
            throw new TaskerException("Error when send email", INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String sendMailWithAttachment(EmailDetailRequest details) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(details.getSubject());

            FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));

            mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        } catch (Exception e) {
            throw new TaskerException("Error when send email", INTERNAL_SERVER_ERROR);
        }
    }
}
