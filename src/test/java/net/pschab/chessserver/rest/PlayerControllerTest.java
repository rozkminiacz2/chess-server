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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static net.pschab.chessserver.TestPlayerHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
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
        when(playerService.addNewPlayer(createTestPlayer(PLAYER_NAME.concat("4")))).thenReturn(true);
        when(playerService.changePassword(createTestPlayer(PLAYER_NAME.concat("6")))).thenReturn(true);
        when(playerService.changeRole(createTestPlayer(PLAYER_NAME.concat("6")))).thenReturn(true);
        when(playerService.deletePlayer(PLAYER_NAME.concat("5"))).thenReturn(true);
    }

    @Test()
    public void getAllPlayers() {
        ResponseEntity<Player[]> response = template.getForEntity("/tttserver/players/get-all", Player[].class);

        List<Player> playerList = Arrays.asList(Objects.requireNonNull(response.getBody()));
        assertThat(playerList).isNotNull().isNotEmpty().hasSize(3);
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("1")));
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("2")));
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("3")));
    }

    @Test()
    public void getOnePlayer() {
        Map<String, String> params = new HashMap<>();
        params.put("name", PLAYER_NAME.concat("6"));

        ResponseEntity<Player> response = template.getForEntity("/tttserver/players/get-by-id?name={name}", Player.class, params);

        Player player = response.getBody();
        assertThat(player).isNotNull();
        assertThat(player.getName()).isEqualTo(PLAYER_NAME.concat("6"));
        assertThat(player.getPassword()).isEqualTo(PLAYER_PASSWORD);
        assertThat(player.getRole()).isEqualTo(Role.USER);
    }

    @Test()
    public void addPlayer() {
        Player playerToAdd = createTestPlayer(PLAYER_NAME.concat("4"));

        ResponseEntity<Boolean> response = template.postForEntity("/tttserver/players/add", playerToAdd, Boolean.class);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isTrue();
    }

    @Test()
    public void changePassword() {
        Player playerToHavePasswordChanged = createTestPlayer(PLAYER_NAME.concat("6"));

        ResponseEntity<Boolean> response = template.postForEntity("/tttserver/players/change-password", playerToHavePasswordChanged, Boolean.class);

        assertThat(response.getBody()).isNotNull().isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test()
    public void changeRole() {
        Player playerToHaveRoleChanged = createTestPlayer(PLAYER_NAME.concat("6"));

        ResponseEntity<Boolean> response = template.postForEntity("/tttserver/players/change-role", playerToHaveRoleChanged, Boolean.class);

        assertThat(response.getBody()).isNotNull().isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test()
    public void deletePlayer() {
        Map<String, String> params = new HashMap<>();
        params.put("name", PLAYER_NAME.concat("5"));

        ResponseEntity<Boolean> response = template.getForEntity("/tttserver/players/delete?name={name}", Boolean.class, params);

        Boolean booleanResponse = response.getBody();
        assertThat(booleanResponse).isNotNull().isTrue();
    }

}
