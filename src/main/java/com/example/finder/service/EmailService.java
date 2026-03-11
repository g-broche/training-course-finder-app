package com.example.finder.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.finder.model.AppUser;
import com.example.finder.utils.SanitizerUtil;

@Service
public class EmailService {
    private String appDomain;
    private JavaMailSender mailSender;
    private SanitizerUtil sanitizerUtil;

    public EmailService(JavaMailSender mailSender,
            @Value("${api.domain}") String appDomain,
            SanitizerUtil sanitizerUtil) {
        this.mailSender = mailSender;
        this.appDomain = appDomain;
        this.sanitizerUtil = sanitizerUtil;
    }

    /**
     * Sends post registration mail allowing the user to validate their account.
     * 
     * @param user
     * @param token
     * @throws MessagingException
     */
    public void sendRegistrationActivationEmail(AppUser user, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("noreply@retriever.com");
        helper.setTo(user.getEmail());
        helper.setSubject("Registration Validation Link");

        String htmlContent = generateRegistrationMailContent(user, token);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    /**
     * Sends post confirmation of verification mail.
     * 
     * @param user
     * @param token
     * @throws MessagingException
     */
    public void sendConfirmedVerificationEmail(AppUser user) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("noreply@retriever.com");
        helper.setTo(user.getEmail());
        helper.setSubject("Account Activation Confirmation");

        String htmlContent = generateConfirmedVerificationMailContent(user);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    /**
     * Generate post registration activation mail content
     * 
     * @param user
     * @param token
     * @return Stringified html content
     */
    private String generateRegistrationMailContent(AppUser user, String token) {
        String activationLink = appDomain + "/api/auth/activate/" + token;
        String sanitizedUserName = sanitizerUtil.sanitizeForHtml(user.getFirstName());
        String htmlContent = "<html>" +
                "<body>" +
                "<h2>Welcome!</h2>" +
                "<p>Thanks for registering " + sanitizedUserName
                + ". Please click the link below to fully activate your account:</p>" +
                "<a href=\"" + activationLink + "\">Activate Account</a>" +
                "<br/><br/>" +
                "<p>If you did not register, please ignore this email.</p>" +
                "</body>" +
                "</html>";
        return htmlContent;
    }

    /**
     * Generate confirmed verification mail content
     * 
     * @param user
     * @return Stringified html content
     */
    private String generateConfirmedVerificationMailContent(AppUser user) {
        String sanitizedUserName = sanitizerUtil.sanitizeForHtml(user.getFirstName());
        String htmlContent = "<html>" +
                "<body>" +
                "<p>Thanks for registering " + sanitizedUserName
                + ". Your account has been successfully activated.</p>" +
                "</body>" +
                "</html>";
        return htmlContent;
    }
}
