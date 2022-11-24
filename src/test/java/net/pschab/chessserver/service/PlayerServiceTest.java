package net.pschab.chessserver.service;

import net.pschab.chessserver.entity.Player;
import net.pschab.chessserver.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static net.pschab.chessserver.TestPlayerHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PlayerServiceTest {

    @MockBean
    PlayerRepository playerRepository;
    @Autowired
    PlayerService playerService;

    @BeforeEach
    public void initialize() {
        when(playerRepository.findAll()).thenReturn(getAllPlayerList());
        when(playerRepository.findById(TEST_PLAYER_NAME.concat("5")))
                .thenReturn(Optional.of(createPlayer(TEST_PLAYER_NAME.concat("5"))));
        when(playerRepository.findById(TEST_PLAYER_NAME.concat("6")))
                .thenReturn(Optional.of(createPlayer(TEST_PLAYER_NAME.concat("6"))));
        when(playerRepository.save(createPlayer(TEST_PLAYER_NAME.concat("4"))))
                .thenReturn(createPlayer(TEST_PLAYER_NAME.concat("4")));
    }

    @Test
    public void shouldGetAllPlayers() {
        List<Player> playerList = playerService.getAllPlayers();

        assertThat(playerList).isNotNull().isNotEmpty().hasSize(3);
        assertThat(playerList).contains(createPlayer(TEST_PLAYER_NAME.concat("1")));
        assertThat(playerList).contains(createPlayer(TEST_PLAYER_NAME.concat("2")));
        assertThat(playerList).contains(createPlayer(TEST_PLAYER_NAME.concat("3")));
    }

    @Test
    public void shouldAddNewPlayer() {
        Boolean status = playerService.addNewPlayer(createPlayer(TEST_PLAYER_NAME.concat("4")));

        assertThat(status).isTrue();
    }

    @Test
    public void shouldGetPlayerById() {
        Player playerFound = playerService.getPlayerById(TEST_PLAYER_NAME.concat("5"));

        assertThat(playerFound).isEqualTo(createPlayer(TEST_PLAYER_NAME.concat("5")));
    }

    @Test
    public void shouldThrowExceptionDueToNullNameOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(createPlayer(null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToEmptyNameOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(createPlayer("")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToNullPasswordOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(createPlayer(TEST_PLAYER_NAME.concat("7"), null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToEmptyPasswordOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(createPlayer(TEST_PLAYER_NAME.concat("7"), "")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToNullNameOnGettingPlayerById() {
        assertThatThrownBy(()-> playerService.getPlayerById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToEmptyNameOnGettingPlayerById() {
        assertThatThrownBy(()-> playerService.getPlayerById(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }
    @Test
    public void shouldDeletePlayer() {
        assertThat(playerService.deletePlayer(TEST_PLAYER_NAME.concat("6"))).isTrue();
    }

    @Test
    public void shouldThrowExceptionDueToPlayerNotFoundOnPlayerDeletion() {
        assertThatThrownBy(()-> playerService.deletePlayer(TEST_PLAYER_NAME.concat("7")))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(String.format("Player with name %s does not exist in the database.", TEST_PLAYER_NAME.concat("7")));
    }
}
