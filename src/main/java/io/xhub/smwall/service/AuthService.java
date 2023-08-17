package io.xhub.smwall.service;

import io.xhub.smwall.commands.LoginCommand;
import io.xhub.smwall.constants.ApiClientErrorCodes;
import io.xhub.smwall.domains.User;
import io.xhub.smwall.exceptions.BusinessException;
import io.xhub.smwall.repositories.UserRepository;
import io.xhub.smwall.security.jwt.JwtTokenProvider;
import io.xhub.smwall.utlis.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public String authenticate(LoginCommand loginCommand) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginCommand.getEmail(), loginCommand.getPassword())
        );

        return this.jwtTokenProvider.generateToken(authentication);
    }


    public User getCurrentUser() {
        return SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findFirstByEmailIgnoreCase)
                .orElseThrow(() -> new BusinessException(ApiClientErrorCodes.USER_NOT_FOUND.getErrorMessage()));
    }

    public void updatePassword(String email, String password) {
        log.info("start setting password of user with email : {}", email);
        User user = userRepository.findFirstByEmailIgnoreCase(email)
                .orElseThrow(() -> new BusinessException(ApiClientErrorCodes.USER_NOT_FOUND.getErrorMessage()));

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
