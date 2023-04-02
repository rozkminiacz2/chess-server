package net.pschab.chessserver.rest.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class AuthenticationResource {

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

    public static class AuthenticationRequest {

        @NotNull
        @Size(max = 255)
        private String login;

        @NotNull
        @Size(max = 255)
        private String password;

        public AuthenticationRequest() {
        }

        public AuthenticationRequest(String login, String password) {
            this.login = login;
            this.password = password;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class AuthenticationResponse {

        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}


