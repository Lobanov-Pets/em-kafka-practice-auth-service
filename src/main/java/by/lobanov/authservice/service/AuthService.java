package by.lobanov.authservice.service;

import by.lobanov.authservice.model.*;
import by.lobanov.authservice.repository.*;
import by.lobanov.authservice.util.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;
    private final JwtUtil jwtUtil;
    private static final Random random = new Random();

    public void register(String email) {
        Optional<User> existedUser = userRepository.findByEmail(email);
        User user;
        String code = generateAndReturnCode();

        if (existedUser.isPresent()) {
            if (userRepository.existsByEmailAndConfirmed(email, true)) {
                throw new IllegalArgumentException("Email already confirmed");
            }
            user = existedUser.get();
            user.setConfirmationCode(code);
        } else {
            user = new User();
            user.setEmail(email);
            user.setConfirmationCode(code);
            user.setConfirmed(false);
        }

        userRepository.save(user);
        kafkaProducerService.sendConfirmationCode(email, code);
    }

    private static String generateAndReturnCode() {
        return random.ints(6, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    public String confirm(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isConfirmed()) {
            throw new IllegalStateException("User already confirmed");
        }

        if (!user.getConfirmationCode().equals(code)) {
            throw new IllegalArgumentException("Invalid confirmation code");
        }

        user.setConfirmed(true);
        user.setConfirmationCode(null);
        userRepository.save(user);

        return jwtUtil.generateToken(email);
    }
}