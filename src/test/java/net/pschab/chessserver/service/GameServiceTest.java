package net.pschab.chessserver.service;

import net.pschab.chessserver.model.Game;
import net.pschab.chessserver.repository.GameRepository;
import net.pschab.chessserver.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static net.pschab.chessserver.TestGameHelper.createTestGame;
import static net.pschab.chessserver.TestGameHelper.getThreeGameList;
import static net.pschab.chessserver.TestPlayerHelper.createTestPlayer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GameServiceTest {

    private final Integer gameId = 1;

    @MockBean
    GameRepository gameRepository;

    @MockBean
    PlayerRepository playerRepository;

    @Autowired
    GameService gameService;

    @Test
    public void shouldGetAllGames() {
        when(gameRepository.findAll()).thenReturn(getThreeGameList());

        List<Game> gameList = gameService.getAllGames();

        assertThat(gameList).isNotNull().isNotEmpty().hasSize(3);
        assertThat(gameList).contains(createTestGame(1));
        assertThat(gameList).contains(createTestGame(2));
        assertThat(gameList).contains(createTestGame(3));
    }


    @Test
    public void shouldExecutePasswordChangeOperation() {
        when(gameRepository.findById(gameId))
                .thenReturn(Optional.of(createTestGame(gameId)));

        assertThat(gameService.updateGame(createTestGame(gameId))).isEqualTo(true);
    }

    @Test
    public void shouldThrowExceptionOnPasswordChangeDueToNoPlayerExists() {
        when(gameRepository.findById(gameId))
                .thenReturn(Optional.empty());

        assertThat(gameService.updateGame(createTestGame(gameId))).isEqualTo(false);
    }

    @Test
    public void shouldGetGameById() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(createTestGame(gameId)));

        Optional<Game> gameOptional = gameService.getGameById(gameId);

        assertThat(gameOptional.orElse(null)).isEqualTo(createTestGame(gameId));
    }

    @Test
    public void shouldThrowExceptionDueToHostPlayerNotFoundOnGameCreation() {
        String hostName = "host";
        String guestName = "guest";
        when(playerRepository.findById(hostName)).thenReturn(Optional.empty());
        when(playerRepository.findById(guestName)).thenReturn(Optional.of(createTestPlayer(guestName)));

        assertThatThrownBy(() -> gameService.addNewGame(createTestGame(gameId, hostName, guestName)))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Host player not found in the database.");
    }

    @Test
    public void shouldThrowExceptionDueToGuestPlayerNotFoundOnGameCreation() {
        String hostName = "host";
        String guestName = "guest";
        when(playerRepository.findById(hostName)).thenReturn(Optional.of(createTestPlayer(hostName)));
        when(playerRepository.findById(guestName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.addNewGame(createTestGame(gameId, hostName, guestName)))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Guest player not found in the database.");
    }

    @Test
    public void shouldThrowExceptionDueToNullIdOnGettingGameById() {
        assertThatThrownBy(() -> gameService.getGameById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Game id cannot be null.");
    }

    @Test
    public void shouldDeleteGame() {
        when(gameRepository.findById(gameId))
                .thenReturn(Optional.of(createTestGame(gameId)));

        assertThat(gameService.deleteGame(gameId)).isTrue();
    }

    @Test
    public void shouldThrowExceptionDueToGameNotFoundOnDeletion() {
        assertThatThrownBy(() -> gameService.deleteGame(gameId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(String.format("Game with id %s does not exist in the database.", gameId));
    }
}
