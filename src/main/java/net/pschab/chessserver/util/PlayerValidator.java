package net.pschab.chessserver.util;

import org.springframework.stereotype.Service;

@Service
public class PlayerValidator {

    public void validatePlayerName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be empty.");
        }
    }

    public void validatePlayerPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Player password cannot be empty.");
        }
    }
}
