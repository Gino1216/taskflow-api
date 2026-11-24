package dev.portfolio.taskflow.auth;

import dev.portfolio.taskflow.common.ConflictException;
import dev.portfolio.taskflow.user.UserAccount;
import dev.portfolio.taskflow.user.UserAccountRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokens;

    public AuthService(UserAccountRepository users, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, TokenService tokens) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokens = tokens;
    }

    @Transactional
    public AuthDtos.TokenResponse register(AuthDtos.RegisterRequest request) {
        if (users.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Email is already registered");
        }
        UserAccount user = new UserAccount(
                request.email().trim(),
                passwordEncoder.encode(request.password()),
                request.displayName().trim());
        return tokens.issue(users.save(user));
    }

    public AuthDtos.TokenResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password()));
        UserAccount user = users.findByEmailIgnoreCase(request.email()).orElseThrow();
        return tokens.issue(user);
    }
}
