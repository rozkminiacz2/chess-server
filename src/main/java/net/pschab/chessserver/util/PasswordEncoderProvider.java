package net.pschab.chessserver.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderProvider {

    private static PasswordEncoderProvider instance;
    private final PasswordEncoder passwordEncoder;

    private PasswordEncoderProvider() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    public static synchronized PasswordEncoderProvider getInstance() {
        if (instance == null) {
            instance = new PasswordEncoderProvider();
        }
        return instance;
    }

    public PasswordEncoder provide() {
        return passwordEncoder;
    }
}
