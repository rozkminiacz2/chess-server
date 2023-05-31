package net.pschab.chessserver.rest;

import net.pschab.chessserver.model.Game;
import net.pschab.chessserver.model.Status;
import net.pschab.chessserver.service.GameService;
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

import static net.pschab.chessserver.TestGameHelper.createTestGame;
import static net.pschab.chessserver.TestGameHelper.getThreeGameList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("insecure")
public class GameControllerTest {

    private final Integer gameId = 1;
    private final Integer wrongId = 2;
    @Autowired
    private TestRestTemplate template;

    @MockBean
    GameService gameService;

    @Test()
    public void shouldRetrieveAllGamesInDatabase() {
        when(gameService.getAllGames()).thenReturn(getThreeGameList());

        ResponseEntity<CollectionModel<EntityModel<Game>>> response =
                template.exchange("/games", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Game> games = getGameList(response.getBody());
        assertThat(games).isNotNull().isNotEmpty().hasSize(3);
        assertThat(games).contains(createTestGame(1));
        assertThat(games).contains(createTestGame(2));
        assertThat(games).contains(createTestGame(3));

    }

    private static List<Game> getGameList(CollectionModel<EntityModel<Game>> collectionModel) {
        return Objects.requireNonNull(collectionModel).getContent().stream()
                .map(EntityModel::getContent)
                .filter(Objects::nonNull)
                .toList();
    }

    @Test()
    public void shouldRetrieveAssumedNoneGamesInDatabase() {
        when(gameService.getAllGames()).thenReturn(Collections.emptyList());

        ResponseEntity<ApiError> response = template.getForEntity("/games", ApiError.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiError expectedError =
                new ApiError(HttpStatus.NOT_FOUND, "The item does not exist.", "There are no games in the database.");
        assertThat(response.getBody()).isEqualTo(expectedError);
    }

    @Test()
    public void shouldRetrieveOneSingleGame() {
        when(gameService.getGameById(gameId)).thenReturn(Optional.of(createTestGame(gameId)));
        Map<String, Integer> params = createParams(gameId);

        ResponseEntity<Game> response = template.getForEntity("/games/{id}", Game.class, params);

        assertThat(response.getBody()).isNotNull().isEqualTo(createTestGame(gameId));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test()
    public void shouldNotGetAnyGameDueNoGameWithIdExists() {
        when(gameService.getGameById(gameId)).thenReturn(Optional.empty());
        Map<String, Integer> params = createParams(gameId);

        ResponseEntity<ApiError> response = template.getForEntity("/games/{id}", ApiError.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiError expectedError =
                new ApiError(HttpStatus.NOT_FOUND, "The item does not exist.", String.format("There is no game with id: %s in the database.", gameId));
        assertThat(response.getBody()).isEqualTo(expectedError);
    }

    @Test()
    public void shouldAddGameToTheDatabase() {
        Game gameToAdd = createTestGame(gameId);
        when(gameService.addNewGame(gameToAdd)).thenReturn(true);

        ResponseEntity<ApiResponse> response = template.postForEntity("/games", gameToAdd, ApiResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApiResponse expectedResponse = new ApiResponse(HttpStatus.CREATED, String.format("Game with id: %s created.", gameToAdd.getId()));
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test()
    public void shouldNotAddGameToTheDatabaseDueGameWithIdAlreadyExists() {
        Game gameToAdd = createTestGame(gameId);
        when(gameService.addNewGame(gameToAdd)).thenThrow(
                new DuplicateKeyException(String.format("Game with id %s already exists.", gameToAdd)));

        ResponseEntity<ApiError> response = template.postForEntity("/games", gameToAdd, ApiError.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError expectedError =
                new ApiError(HttpStatus.BAD_REQUEST, "Duplicate key.", String.format("Game with id %s already exists.", gameToAdd));
        assertThat(response.getBody()).isEqualTo(expectedError);
    }

    @Test()
    public void shouldUpdateGameHistory() {
        when(gameService.getGameById(gameId)).thenReturn(Optional.of(createTestGame(gameId)));
        when(gameService.updateGame(any(Game.class))).thenReturn(true);
        Map<String, Integer> params = createParams(gameId);
        Game gameToHaveHistoryUpdated = createTestGame(gameId);
        gameToHaveHistoryUpdated.setHistory("updatedHistory");

        ResponseEntity<ApiResponse> response =
                template.exchange("/games/{id}", HttpMethod.PUT, new HttpEntity<>(gameToHaveHistoryUpdated), ApiResponse.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse expectedResponse = new ApiResponse(HttpStatus.OK, String.format("Game with id: %s modified.", gameId));
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test()
    public void shouldNotChangePasswordOfGameDueToInconsistentParameters() {
        Map<String, Integer> params = createParams(wrongId);
        Game gameToBeUpdated = createTestGame(gameId);

        ResponseEntity<ApiError> response =
                template.exchange("/games/{id}", HttpMethod.PUT, new HttpEntity<>(gameToBeUpdated), ApiError.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError expectedError =
                new ApiError(HttpStatus.BAD_REQUEST, "Invalid data.", "Inconsistent game id value provided as service variable.");
        assertThat(response.getBody()).isEqualTo(expectedError);
    }

    @Test()
    public void shouldUpdateGameStatus() {
        when(gameService.getGameById(gameId)).thenReturn(Optional.of(createTestGame(gameId)));
        when(gameService.updateGame(any(Game.class))).thenReturn(true);
        Map<String, Integer> params = createParams(gameId);
        Game gameToHaveStatusUpdated = createTestGame(gameId);
        gameToHaveStatusUpdated.setStatus(Status.STARTED);

        ResponseEntity<ApiResponse> response =
                template.exchange("/games/{id}", HttpMethod.PUT, new HttpEntity<>(gameToHaveStatusUpdated), ApiResponse.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse expectedResponse = new ApiResponse(HttpStatus.OK, String.format("Game with id: %s modified.", gameId));
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }


    @Test()
    public void shouldNotChangeAnythingToGameDueToIdenticalParametersToExistingGame() {
        when(gameService.getGameById(gameId)).thenReturn(Optional.of(createTestGame(gameId)));
        when(gameService.updateGame(any(Game.class))).thenReturn(true);
        Map<String, Integer> params = createParams(gameId);
        Game gameToBeUpdated = createTestGame(gameId);

        ResponseEntity<ApiError> response =
                template.exchange("/games/{id}", HttpMethod.PUT, new HttpEntity<>(gameToBeUpdated), ApiError.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError expectedError =
                new ApiError(HttpStatus.BAD_REQUEST, "Invalid data.", "Nothing to modify: provided game data are identical to data in the database.");
        assertThat(response.getBody()).isEqualTo(expectedError);
    }

    @Test()
    public void shouldDeleteGameById() {
        when(gameService.deleteGame(gameId)).thenReturn(true);
        Map<String, Integer> params = createParams(gameId);

        ResponseEntity<Boolean> response = template.exchange("/games/{id}", HttpMethod.DELETE, new HttpEntity<>(null), Boolean.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test()
    public void shouldNotDeleteGameDueGameDoesNotExist() {
        when(gameService.deleteGame(gameId)).thenThrow(
                new NoSuchElementException(String.format("There is no game with id: %s in the database.", gameId)));
        Map<String, Integer> params = createParams(gameId);

        ResponseEntity<ApiError> response = template.exchange("/games/{id}", HttpMethod.DELETE, new HttpEntity<>(null), ApiError.class, params);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiError expectedError =
                new ApiError(HttpStatus.NOT_FOUND, "The item does not exist.", String.format("There is no game with id: %s in the database.", gameId));
        assertThat(response.getBody()).isEqualTo(expectedError);
    }

    private static Map<String, Integer> createParams(Integer gameId) {
        Map<String, Integer> params = new HashMap<>();
        params.put("id", gameId);
        return params;
    }
}
