package net.pschab.chessserver.security;

import net.pschab.chessserver.rest.security.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(token).isNotNull();
        String username = jwtTokenService.validateTokenAndGetUsername(token);
        assertThat(userDetails.getUsername()).isEqualTo(username);
    }

    @Test
    void validateTokenAndGetUsernameShouldReturnNullForInvalidToken() {
        String username = jwtTokenService.validateTokenAndGetUsername("invalid.token");

        assertThat(username).isNull();
    }
}

