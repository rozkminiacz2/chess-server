package net.pschab.chessserver.security;

import net.pschab.chessserver.rest.security.JwtTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

public class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService("mysecretkey");
    }

    @Test
    void generateTokenShouldReturnValidJwtToken() {
        UserDetails userDetails = new User("john.doe", "mysecretpassword", Collections.emptyList());

        String token = jwtTokenService.generateToken(userDetails);

        Assertions.assertNotNull(token);

        String username = jwtTokenService.validateTokenAndGetUsername(token);
        Assertions.assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void validateTokenAndGetUsernameShouldReturnNullForInvalidToken() {
        String invalidToken = "invalid.token";

        String username = jwtTokenService.validateTokenAndGetUsername(invalidToken);

        Assertions.assertNull(username);
    }
}

