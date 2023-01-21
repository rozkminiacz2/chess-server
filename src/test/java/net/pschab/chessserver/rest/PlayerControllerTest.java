package net.pschab.chessserver.rest;

import net.pschab.chessserver.entity.Player;
import net.pschab.chessserver.entity.Role;
import net.pschab.chessserver.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static net.pschab.chessserver.TestPlayerHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlayerControllerTest {

    @Autowired
    private TestRestTemplate template;

    @MockBean
    PlayerService playerService;

    @BeforeEach
    public void initialize() {
        when(playerService.getAllPlayers()).thenReturn(getThreePlayerList());
        when(playerService.getPlayerById(PLAYER_NAME.concat("6"))).thenReturn(Optional.of(createTestPlayer(PLAYER_NAME.concat("6"))));
        when(playerService.getPlayerById(PLAYER_NAME.concat("4"))).thenReturn(Optional.empty());
        when(playerService.addNewPlayer(createTestPlayer(PLAYER_NAME.concat("4")))).thenReturn(true);
        when(playerService.updatePlayer(any(Player.class))).thenReturn(true);
        when(playerService.deletePlayer(PLAYER_NAME.concat("5"))).thenReturn(true);
    }

    @Test()
    public void shouldRetrieveAssumedAllPlayersInDatabase() {
        ResponseEntity<Player[]> response = template.getForEntity("/player", Player[].class);

        List<Player> playerList = Arrays.asList(Objects.requireNonNull(response.getBody()));
        assertThat(playerList).isNotNull().isNotEmpty().hasSize(3);
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("1")));
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("2")));
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("3")));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test()
    public void shouldRetrieveOneSpecifiedPlayer() {
        Map<String, String> params = new HashMap<>();
        params.put("name", PLAYER_NAME.concat("6"));

        ResponseEntity<Player> response = template.getForEntity("/player/{name}", Player.class, params);

        Player player = response.getBody();
        assertThat(player).isNotNull();
        assertThat(player.getName()).isEqualTo(PLAYER_NAME.concat("6"));
        assertThat(player.getPassword()).isEqualTo(PLAYER_PASSWORD);
        assertThat(player.getRole()).isEqualTo(Role.USER);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test()
    public void shouldAddPlayerToTheDatabase() {
        Player playerToAdd = createTestPlayer(PLAYER_NAME.concat("4"));

        ResponseEntity<Boolean> response = template.postForEntity("/player", playerToAdd, Boolean.class);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test()
    public void shouldChangePasswordOfPlayer() {
        Map<String, String> params = new HashMap<>();
        params.put("name", PLAYER_NAME.concat("6"));
        Player playerToHavePasswordChanged = createTestPlayer(PLAYER_NAME.concat("6"), "newPassword123");

        ResponseEntity<Boolean> response = template.exchange("/player/{name}", HttpMethod.PUT, new HttpEntity<>(playerToHavePasswordChanged), Boolean.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test()
    public void shouldChangeRoleOfPlayer() {
        Map<String, String> params = new HashMap<>();
        params.put("name", PLAYER_NAME.concat("6"));
        Player playerToHaveRoleChanged = createTestPlayer(PLAYER_NAME.concat("6"), Role.ADMIN);

        ResponseEntity<Boolean> response = template.exchange("/player/{name}", HttpMethod.PUT, new HttpEntity<>(playerToHaveRoleChanged), Boolean.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test()
    public void shouldDeletePlayerByName() {
        Map<String, String> params = new HashMap<>();
        params.put("name", PLAYER_NAME.concat("5"));

        ResponseEntity<Boolean> response = template.exchange("/player/{name}", HttpMethod.DELETE, new HttpEntity<>(null), Boolean.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
