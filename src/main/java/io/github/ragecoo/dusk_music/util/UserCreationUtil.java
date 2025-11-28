package io.github.ragecoo.dusk_music.util;

import io.github.ragecoo.dusk_music.model.User;
import io.github.ragecoo.dusk_music.model.enums.Role;
import io.github.ragecoo.dusk_music.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Утилита для создания тестового пользователя
 * Создает пользователя nikita123 с корректно захешированным паролем
 */
@Component
@RequiredArgsConstructor
public class UserCreationUtil implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String username = "nikita123";
        String email = "nikita123@gmail.com";
        String password = "nikita123";

        // Проверяем, существует ли пользователь
        if (userRepository.existsByUsername(username)) {
            System.out.println("Пользователь " + username + " уже существует. Пропускаем создание.");
            return;
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            System.out.println("Пользователь с email " + email + " уже существует. Пропускаем создание.");
            return;
        }

        // Создаем нового пользователя
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        // Хешируем пароль с помощью BCrypt
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setSubActive(false);

        // Сохраняем в базу данных
        user = userRepository.save(user);

        System.out.println("========================================");
        System.out.println("Пользователь успешно создан!");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("ID: " + user.getId());
        System.out.println("Role: " + user.getRole());
        System.out.println("========================================");
    }
}

