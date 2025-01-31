package uz.pdp.service;

import org.springframework.stereotype.Component;
import uz.pdp.repository.UserStatusRepository;
import uz.pdp.utils.EmailSender;

import java.util.UUID;

@Component
public class AccountActivationService {
    private final UserStatusRepository userStatusRepository;
    private final EmailSender emailSender;

    public AccountActivationService(UserStatusRepository userStatusRepository, EmailSender emailSender) {
        this.userStatusRepository = userStatusRepository;
        this.emailSender = emailSender;
    }

    public void generateLinkAndSendToEmail(Integer userId, String email) {
        String confirmationCode = UUID.randomUUID().toString();
        userStatusRepository.save(userId, confirmationCode);

        String confirmationLink = "http://localhost:8080/auth/register/confirm/account/" + confirmationCode;
        emailSender.sendConfirmationLink(email, confirmationLink);
    }
}
