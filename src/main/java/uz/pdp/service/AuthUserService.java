package uz.pdp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.pdp.dto.RegisterUserDTO;
import uz.pdp.model.AuthUser;
import uz.pdp.repository.UserRepository;
import uz.pdp.repository.UserStatusRepository;
import uz.pdp.utils.Utils;

@Component
@RequiredArgsConstructor
public class AuthUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountActivationService accountActivationService;
    private final UserStatusRepository userStatusRepository;

    public void save(RegisterUserDTO dto, boolean isCreatedActive) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Email already in use !!!");
        }
        if (dto.name().isBlank()) {
            throw new RuntimeException("Name is empty !!!");
        }

        if (dto.gender().isBlank()) {
            throw new RuntimeException("Gender is empty !!!");
        }
        if (!Utils.isValidEmail(dto.email())) {
            throw new RuntimeException("Invalid email !!!");
        }
        if (!Utils.isValidPassword(dto.password())) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        AuthUser user = AuthUser.builder()
                .name(dto.name())
                .age(dto.age())
                .gender(dto.gender())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .build();

        Integer savedUserId = userRepository.save(user);
        if (!isCreatedActive) {
            accountActivationService.generateLinkAndSendToEmail(savedUserId, dto.email());
        }
    }

    public boolean activateAccount(String confirmationCode) {
        boolean isFound = userStatusRepository.findByCode(confirmationCode);
        if (isFound) {
            userStatusRepository.delete(confirmationCode);
            return true;
        } else {
            return false;
        }
    }


}
