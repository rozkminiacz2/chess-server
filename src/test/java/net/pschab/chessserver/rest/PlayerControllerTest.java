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
import org.springframework.dao.DuplicateKeyException;
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
        when(playerService.updatePlayer(any(Player.class))).thenReturn(true);
    }

    @Test()
    public void shouldRetrieveAssumedAllPlayersInDatabase() {
        when(playerService.getAllPlayers()).thenReturn(getThreePlayerList());

        ResponseEntity<Player[]> response = template.getForEntity("/player", Player[].class);

        List<Player> playerList = Arrays.asList(Objects.requireNonNull(response.getBody()));
        assertThat(playerList).isNotNull().isNotEmpty().hasSize(3);
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("1")));
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("2")));
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("3")));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test()
    public void shouldRetrieveAssumedNonePlayersInDatabase() {
        when(playerService.getAllPlayers()).thenReturn(Collections.emptyList());

        ResponseEntity<ApiError> response = template.getForEntity("/player", ApiError.class);

        ApiError apiError = Objects.requireNonNull(response.getBody());
        assertThat(apiError).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(apiError.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(apiError.getMessage()).isEqualTo("The item does not exist.");
        assertThat(apiError.getDebugMessage()).isEqualTo("There are no players in the database.");
    }

    @Test()
    public void shouldRetrieveOneSinglePlayer() {
        String playerName = PLAYER_NAME.concat("3");
        when(playerService.getPlayerById(playerName)).thenReturn(Optional.of(createTestPlayer(playerName)));
        Map<String, String> params = createParams(playerName);

        ResponseEntity<Player> response = template.getForEntity("/player/{name}", Player.class, params);

        Player player = response.getBody();
        assertThat(player).isNotNull();
        assertThat(player.getName()).isEqualTo(playerName);
        assertThat(player.getPassword()).isEqualTo(PLAYER_PASSWORD);
        assertThat(player.getRole()).isEqualTo(Role.USER);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test()
    public void shouldNotGetAnyPlayerDueNoPlayerWithNameExists() {
        String playerName = PLAYER_NAME.concat("3");
        when(playerService.getPlayerById(playerName)).thenReturn(Optional.empty());
        Map<String, String> params = createParams(playerName);

        ResponseEntity<ApiError> response = template.getForEntity("/player/{name}", ApiError.class, params);

        ApiError apiError = Objects.requireNonNull(response.getBody());
        assertThat(apiError).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(apiError.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(apiError.getMessage()).isEqualTo("The item does not exist.");
        assertThat(apiError.getDebugMessage())
                .isEqualTo(String.format("There is no player with name: %s in the database.", playerName));
    }

    @Test()
    public void shouldAddPlayerToTheDatabase() {
        String playerName = PLAYER_NAME.concat("4");
        Player playerToAdd = createTestPlayer(playerName);
        when(playerService.addNewPlayer(createTestPlayer(playerName))).thenReturn(true);

        ResponseEntity<ApiResponse> response = template.postForEntity("/player", playerToAdd, ApiResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ApiResponse.class);
        ApiResponse apiResponse = response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(HttpStatus.CREATED);
        assertThat(apiResponse.getMessage()).isEqualTo(String.format("Player with name: %s created.", playerName));
    }

    @Test()
    public void shouldNotAddPlayerToTheDatabaseDuePlayerWithNameAlreadyExists() {
        String playerName = PLAYER_NAME.concat("4");
        Player playerToAdd = createTestPlayer(playerName);
        when(playerService.addNewPlayer(createTestPlayer(PLAYER_NAME.concat("4")))).thenThrow(
                new DuplicateKeyException(String.format("Player with name %s already exists.", playerName)));

        ResponseEntity<ApiError> response = template.postForEntity("/player", playerToAdd, ApiError.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ApiError.class);
        ApiError apiError = response.getBody();
        assertThat(apiError.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(apiError.getMessage()).isEqualTo("Duplicate key.");
        assertThat(apiError.getDebugMessage())
                .isEqualTo(String.format("Player with name %s already exists.", playerName));
    }

    @Test()
    public void shouldChangePasswordOfPlayer() {
        String playerName = PLAYER_NAME.concat("6");
        when(playerService.getPlayerById(playerName)).thenReturn(Optional.of(createDbPlayer(playerName)));
        Map<String, String> params = createParams(playerName);
        Player playerToHavePasswordChanged = createTestPlayer(playerName, "newPassword123");

        ResponseEntity<ApiResponse> response =
                template.exchange("/player/{name}", HttpMethod.PUT, new HttpEntity<>(playerToHavePasswordChanged), ApiResponse.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ApiResponse.class);
        ApiResponse apiResponse = response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(apiResponse.getMessage()).isEqualTo(String.format("Player with name: %s modified.", playerName));
    }

    @Test()
    public void shouldNotChangePasswordOfPlayerDueToInconsistentParameters() {
        String playerName = PLAYER_NAME.concat("6");
        Map<String, String> params = createParams(playerName.concat("1"));
        Player playerToHavePasswordChanged = createTestPlayer(playerName, "newPassword123");

        ResponseEntity<ApiError> response =
                template.exchange("/player/{name}", HttpMethod.PUT, new HttpEntity<>(playerToHavePasswordChanged), ApiError.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ApiError.class);
        ApiError apiError = response.getBody();
        assertThat(apiError.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(apiError.getMessage()).isEqualTo("Invalid data.");
        assertThat(apiError.getDebugMessage())
                .isEqualTo("Inconsistent player name value provided as service variable.");
    }

    @Test()
    public void shouldChangeRoleOfPlayer() {
        String playerName = PLAYER_NAME.concat("6");
        when(playerService.getPlayerById(playerName)).thenReturn(Optional.of(createDbPlayer(playerName)));
        Map<String, String> params = createParams(playerName);
        Player playerToHaveRoleChanged = createTestPlayer(playerName, Role.ADMIN);

        ResponseEntity<ApiResponse> response =
                template.exchange("/player/{name}", HttpMethod.PUT, new HttpEntity<>(playerToHaveRoleChanged), ApiResponse.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ApiResponse.class);
        ApiResponse apiResponse = response.getBody();
        assertThat(apiResponse.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(apiResponse.getMessage()).isEqualTo(String.format("Player with name: %s modified.", playerName));
    }

    @Test()
    public void shouldNotChangeAnythingToPlayerDueToIdenticalParametersToExistingPlayer() {
        String playerName = PLAYER_NAME.concat("6");
        when(playerService.getPlayerById(playerName)).thenReturn(Optional.of(createDbPlayer(playerName)));
        Map<String, String> params = createParams(playerName);
        Player playerToHavePasswordChanged = createTestPlayer(playerName);

        ResponseEntity<ApiError> response =
                template.exchange("/player/{name}", HttpMethod.PUT, new HttpEntity<>(playerToHavePasswordChanged), ApiError.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ApiError.class);
        ApiError apiError = response.getBody();
        assertThat(apiError.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(apiError.getMessage()).isEqualTo("Invalid data.");
        assertThat(apiError.getDebugMessage())
                .isEqualTo("Nothing to modify: provided player data are identical to data in the database.");
    }

    @Test()
    public void shouldDeletePlayerByName() {
        String playerName = PLAYER_NAME.concat("5");
        when(playerService.deletePlayer(playerName)).thenReturn(true);
        Map<String, String> params = createParams(playerName);

        ResponseEntity<Boolean> response = template.exchange("/player/{name}", HttpMethod.DELETE, new HttpEntity<>(null), Boolean.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test()
    public void shouldNotDeletePlayerDuePlayerDoesNotExist() {
        String playerName = PLAYER_NAME.concat("5");
        when(playerService.deletePlayer(playerName)).thenThrow(
                new NoSuchElementException(String.format("There is no player with name: %s in the database.", playerName)));
        Map<String, String> params = createParams(playerName);

        ResponseEntity<ApiError> response = template.exchange("/player/{name}", HttpMethod.DELETE, new HttpEntity<>(null), ApiError.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull().isInstanceOf(ApiError.class);
        ApiError apiError = response.getBody();
        assertThat(apiError.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(apiError.getMessage()).isEqualTo("The item does not exist.");
        assertThat(apiError.getDebugMessage())
                .isEqualTo(String.format("There is no player with name: %s in the database.", playerName));
    }

    private static Map<String, String> createParams(String playerName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", playerName);
        return params;
    }
}
