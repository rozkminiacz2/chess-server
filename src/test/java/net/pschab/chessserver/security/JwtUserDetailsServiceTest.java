package net.pschab.chessserver.security;

import static net.pschab.chessserver.TestPlayerHelper.createTestPlayer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import net.pschab.chessserver.model.Role;
import net.pschab.chessserver.rest.security.JwtUserDetailsService;
import net.pschab.chessserver.service.PlayerService;
import org.junit.jupiter.api.AfterEach;
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

    private AutoCloseable closeable;

    @BeforeEach
    public void initMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void shouldReturnJwtUserDetailsForCorrectUser() {
        when(playerService.getPlayerByName(USERNAME)).thenReturn(Optional.of(createTestPlayer(USERNAME,PASSWORD)));

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(USERNAME);

        assertThat(USERNAME).isEqualTo(userDetails.getUsername());
        assertThat(PASSWORD).isEqualTo(userDetails.getPassword());
        assertThat(Optional.of(new SimpleGrantedAuthority(Role.USER.toString())))
                .isEqualTo(userDetails.getAuthorities().stream().findFirst());
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionOnWrongUser() {
        when(playerService.getPlayerByName(USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jwtUserDetailsService.loadUserByUsername(USERNAME))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(String.format("User %s not found", USERNAME));
    }
}

