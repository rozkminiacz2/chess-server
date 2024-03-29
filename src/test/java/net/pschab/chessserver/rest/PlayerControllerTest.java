package net.pschab.chessserver.rest;

import net.pschab.chessserver.model.Player;
import net.pschab.chessserver.model.Role;
import net.pschab.chessserver.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static net.pschab.chessserver.TestPlayerHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("insecure")
public class PlayerControllerTest {

    private final String playerName = PLAYER_NAME.concat("1");
    @Autowired
    private TestRestTemplate template;

    @MockBean
    PlayerService playerService;

    @Test()
    public void shouldRetrieveAllPlayersInDatabase() {
        when(playerService.getAllPlayers()).thenReturn(getThreePlayerList());

        ResponseEntity<CollectionModel<EntityModel<Player>>> response =
                template.exchange("/players", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Player> playerList = getPlayerList(response.getBody());
        assertThat(playerList).isNotNull().isNotEmpty().hasSize(3);
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("1")));
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("2")));
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("3")));

    }

    @Test()
    public void shouldRetrieveAssumedNonePlayersInDatabase() {
        when(playerService.getAllPlayers()).thenReturn(Collections.emptyList());

        ResponseEntity<ApiError> response = template.getForEntity("/players", ApiError.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiError expectedError =
                new ApiError(HttpStatus.NOT_FOUND, "The item does not exist.", "There are no players in the database.");
        assertThat(response.getBody()).isEqualTo(expectedError);
    }

    @Test()
    public void shouldRetrieveOneSinglePlayer() {
        when(playerService.getPlayerByName(playerName)).thenReturn(Optional.of(createTestPlayer(playerName)));
        Map<String, String> params = createParams(playerName);

        ResponseEntity<Player> response = template.getForEntity("/players/{name}", Player.class, params);

        assertThat(response.getBody()).isEqualTo(createTestPlayer(playerName));
    }

    @Test()
    public void shouldNotGetAnyPlayerDueNoPlayerWithNameExists() {
        when(playerService.getPlayerByName(playerName)).thenReturn(Optional.empty());
        Map<String, String> params = createParams(playerName);

        ResponseEntity<ApiError> response = template.getForEntity("/players/{name}", ApiError.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiError expectedError =
                new ApiError(HttpStatus.NOT_FOUND, "The item does not exist.", String.format("There is no player with name: %s in the database.", playerName));
        assertThat(response.getBody()).isEqualTo(expectedError);
    }

    @Test()
    public void shouldAddPlayerToTheDatabase() {
        Player playerToAdd = createTestPlayer(playerName);
        when(playerService.addNewPlayer(createTestPlayer(playerName))).thenReturn(true);

        ResponseEntity<ApiResponse> response = template.postForEntity("/players", playerToAdd, ApiResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApiResponse expectedResponse = new ApiResponse(HttpStatus.CREATED, String.format("Player with name: %s created.", playerName));
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test()
    public void shouldNotAddPlayerToTheDatabaseDuePlayerWithNameAlreadyExists() {
        Player playerToAdd = createTestPlayer(playerName);
        when(playerService.addNewPlayer(createTestPlayer(playerName))).thenThrow(
                new DuplicateKeyException(String.format("Player with name %s already exists.", playerName)));

        ResponseEntity<ApiError> response = template.postForEntity("/players", playerToAdd, ApiError.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError expectedError =
                new ApiError(HttpStatus.BAD_REQUEST, "Duplicate key.", String.format("Player with name %s already exists.", playerName));
        assertThat(response.getBody()).isEqualTo(expectedError);
    }

    @Test()
    public void shouldChangePasswordOfPlayer() {
        when(playerService.getPlayerByName(playerName)).thenReturn(Optional.of(createDbPlayer(playerName)));
        when(playerService.updatePlayer(any(Player.class))).thenReturn(true);
        Map<String, String> params = createParams(playerName);
        Player playerToHavePasswordChanged = createTestPlayer(playerName, "newPassword123");

        ResponseEntity<ApiResponse> response =
                template.exchange("/players/{name}", HttpMethod.PUT, new HttpEntity<>(playerToHavePasswordChanged), ApiResponse.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse expectedResponse = new ApiResponse(HttpStatus.OK, String.format("Player with name: %s modified.", playerName));
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test()
    public void shouldNotChangePasswordOfPlayerDueToInconsistentParameters() {
        Map<String, String> params = createParams(playerName.concat("1"));
        Player playerToHavePasswordChanged = createTestPlayer(playerName, "newPassword123");

        ResponseEntity<ApiError> response =
                template.exchange("/players/{name}", HttpMethod.PUT, new HttpEntity<>(playerToHavePasswordChanged), ApiError.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError expectedError =
                new ApiError(HttpStatus.BAD_REQUEST, "Invalid data.", "Inconsistent player name value provided as service variable.");
        assertThat(response.getBody()).isEqualTo(expectedError);
    }

    @Test()
    public void shouldChangeRoleOfPlayer() {
        when(playerService.getPlayerByName(playerName)).thenReturn(Optional.of(createDbPlayer(playerName)));
        when(playerService.updatePlayer(any(Player.class))).thenReturn(true);
        Map<String, String> params = createParams(playerName);
        Player playerToHaveRoleChanged = createTestPlayer(playerName, Role.ADMIN);

        ResponseEntity<ApiResponse> response =
                template.exchange("/players/{name}", HttpMethod.PUT, new HttpEntity<>(playerToHaveRoleChanged), ApiResponse.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse expectedResponse = new ApiResponse(HttpStatus.OK, String.format("Player with name: %s modified.", playerName));
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test()
    public void shouldNotChangeAnythingToPlayerDueToIdenticalParametersToExistingPlayer() {
        when(playerService.getPlayerByName(playerName)).thenReturn(Optional.of(createDbPlayer(playerName)));
        Map<String, String> params = createParams(playerName);
        Player playerToHavePasswordChanged = createTestPlayer(playerName);

        ResponseEntity<ApiError> response =
                template.exchange("/players/{name}", HttpMethod.PUT, new HttpEntity<>(playerToHavePasswordChanged), ApiError.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError expectedError =
                new ApiError(HttpStatus.BAD_REQUEST, "Invalid data.", "Nothing to modify: provided player data are identical to data in the database.");
        assertThat(response.getBody()).isEqualTo(expectedError);
    }

    @Test()
    public void shouldDeletePlayerByName() {
        when(playerService.deletePlayer(playerName)).thenReturn(true);
        Map<String, String> params = createParams(playerName);

        ResponseEntity<Boolean> response = template.exchange("/players/{name}", HttpMethod.DELETE, new HttpEntity<>(null), Boolean.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test()
    public void shouldNotDeletePlayerDuePlayerDoesNotExist() {
        when(playerService.deletePlayer(playerName)).thenThrow(
                new NoSuchElementException(String.format("There is no player with name: %s in the database.", playerName)));
        Map<String, String> params = createParams(playerName);

        ResponseEntity<ApiError> response = template.exchange("/players/{name}", HttpMethod.DELETE, new HttpEntity<>(null), ApiError.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiError expectedError =
                new ApiError(HttpStatus.NOT_FOUND, "The item does not exist.", String.format("There is no player with name: %s in the database.", playerName));
        assertThat(response.getBody()).isEqualTo(expectedError);
    }

    private static List<Player> getPlayerList(CollectionModel<EntityModel<Player>> collectionModel) {
        return Objects.requireNonNull(collectionModel).getContent().stream()
                .map(EntityModel::getContent)
                .filter(Objects::nonNull)
                .toList();
    }

    private static Map<String, String> createParams(String playerName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", playerName);
        return params;
    }
}
