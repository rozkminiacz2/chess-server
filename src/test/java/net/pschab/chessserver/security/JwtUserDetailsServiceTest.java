package net.pschab.chessserver.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import net.pschab.chessserver.model.Player;
import net.pschab.chessserver.model.Role;
import net.pschab.chessserver.rest.security.JwtUserDetailsService;
import net.pschab.chessserver.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsServiceTest {

    public static final String USERNAME = "TestUser";
    public static final String PASSWORD = "password";

    @InjectMocks
    private JwtUserDetailsService jwtUserDetailsService;

    @Mock
    private PlayerService playerService;

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnJwtUserDetailsForCorrectUser() {
        Player player = new Player();
        player.setName(USERNAME);
        player.setPassword(PASSWORD);
        when(playerService.getPlayerByName(USERNAME)).thenReturn(Optional.of(player));

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(USERNAME);

        assertEquals(USERNAME, userDetails.getUsername());
        assertEquals(PASSWORD, userDetails.getPassword());
        assertEquals(Optional.of(new SimpleGrantedAuthority(Role.USER.toString())), userDetails.getAuthorities().stream().findFirst());
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionOnWrongUser() {
        when(playerService.getPlayerByName(USERNAME)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            jwtUserDetailsService.loadUserByUsername(USERNAME);
        });

        assertEquals(String.format("User %s not found", USERNAME), exception.getMessage());
    }

}

