package com.tavernasorte.service;

import com.tavernasorte.dto.CreateUserRequest;
import com.tavernasorte.dto.CreateUserResponse;
import com.tavernasorte.database.entity.User;
import com.tavernasorte.exception.EmailAlreadyInUseException;
import com.tavernasorte.database.repository.UserRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CreateUserResponse create(CreateUserRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException();
        }

        User user = new User(
                request.getName().trim(),
                email,
                passwordEncoder.encode(request.getPassword())
        );

        return toResponse(userRepository.save(user));
    }

    private CreateUserResponse toResponse(User user) {
        return new CreateUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
