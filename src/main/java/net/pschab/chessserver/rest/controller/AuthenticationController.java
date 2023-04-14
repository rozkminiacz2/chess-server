package net.pschab.chessserver.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.pschab.chessserver.rest.security.JwtTokenService;
import net.pschab.chessserver.rest.security.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenService jwtTokenService;

    @PostMapping("/authenticate")
    public AuthenticationResponse authenticate(@RequestBody @Valid final AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(getAuthentication(authenticationRequest));
        } catch (final BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.getLogin());
        return getAuthenticationResponse(userDetails);
    }

    private static UsernamePasswordAuthenticationToken getAuthentication(AuthenticationRequest authenticationRequest) {
        return new UsernamePasswordAuthenticationToken(
                authenticationRequest.getLogin(), authenticationRequest.getPassword());
    }

    private AuthenticationResponse getAuthenticationResponse(UserDetails userDetails) {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccessToken(jwtTokenService.generateToken(userDetails));
        return authenticationResponse;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthenticationRequest {

        @NotNull
        @Size(max = 255)
        private String login;

        @NotNull
        @Size(max = 255)
        private String password;
    }

    @Data
    public static class AuthenticationResponse {

        private String accessToken;

    }
}


