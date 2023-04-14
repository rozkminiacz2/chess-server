package net.pschab.chessserver.rest;

import net.pschab.chessserver.rest.controller.AuthenticationController;
import net.pschab.chessserver.rest.controller.AuthenticationController.AuthenticationRequest;
import net.pschab.chessserver.rest.controller.AuthenticationController.AuthenticationResponse;
import net.pschab.chessserver.rest.security.JwtTokenService;
import net.pschab.chessserver.rest.security.JwtUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    public static final String USERNAME = "TestUser";
    public static final String VALID_PASSWORD = "validPassword";
    public static final String INVALID_PASSWORD = "invalidPassword";
    public static final String TOKEN = "token";

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUserDetailsService jwtUserDetailsService;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void testAuthenticate() {
        // given
        AuthenticationRequest authenticationRequest = createAuthenticationRequest(USERNAME, VALID_PASSWORD);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(USERNAME, VALID_PASSWORD);
        given(authenticationManager.authenticate(authenticationToken)).willReturn(null);

        UserDetails userDetails = new User(USERNAME, VALID_PASSWORD, Collections.emptyList());
        given(jwtUserDetailsService.loadUserByUsername(USERNAME)).willReturn(userDetails);

        given(jwtTokenService.generateToken(userDetails)).willReturn(TOKEN);

        // when
        AuthenticationResponse authenticationResponse = authenticationController.authenticate(authenticationRequest);

        // then
        assertThat(authenticationResponse).isNotNull();
        assertThat(authenticationResponse.getAccessToken()).isEqualTo(TOKEN);

        verify(authenticationManager).authenticate(authenticationToken);
        verify(jwtUserDetailsService).loadUserByUsername(USERNAME);
        verify(jwtTokenService).generateToken(userDetails);
    }

    private static AuthenticationRequest createAuthenticationRequest(String name, String password) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setLogin(name);
        authenticationRequest.setPassword(password);
        return authenticationRequest;
    }

    @Test
    void testAuthenticateWithInvalidCredentials() {
        // given
        AuthenticationRequest authenticationRequest = createAuthenticationRequest(USERNAME, INVALID_PASSWORD);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(USERNAME, INVALID_PASSWORD);
        given(authenticationManager.authenticate(authenticationToken)).willThrow(new BadCredentialsException("Invalid credentials"));

        // when
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> authenticationController.authenticate(authenticationRequest));

        // then
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(authenticationManager).authenticate(authenticationToken);
        verify(jwtUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenService, never()).generateToken(any(UserDetails.class));
    }
}
