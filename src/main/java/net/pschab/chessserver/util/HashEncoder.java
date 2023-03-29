package net.pschab.chessserver.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

public class HashEncoder {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encode(String text) {
        return encoder.encode(text);
    }

    public static boolean matches(String password, String encodedPassword) {
        return encoder.matches(password, encodedPassword);
    }

}
