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
        when(playerRepository.findAll()).thenReturn(getThreePlayerList());
        when(playerRepository.findById(PLAYER_NAME.concat("5")))
                .thenReturn(Optional.of(createTestPlayer(PLAYER_NAME.concat("5"))));
        when(playerRepository.findById(PLAYER_NAME.concat("6")))
                .thenReturn(Optional.of(createTestPlayer(PLAYER_NAME.concat("6"))));
        when(playerRepository.save(createTestPlayer(PLAYER_NAME.concat("4"))))
                .thenReturn(createTestPlayer(PLAYER_NAME.concat("4")));
    }

    @Test
    public void shouldGetAllPlayers() {
        List<Player> playerList = playerService.getAllPlayers();

        assertThat(playerList).isNotNull().isNotEmpty().hasSize(3);
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("1")));
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("2")));
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("3")));
    }

    @Test
    public void shouldAddNewPlayerWithNoRoleSpecified() {
        Boolean status = playerService.addNewPlayer(createTestPlayer(PLAYER_NAME.concat("4")));

        assertThat(status).isTrue();
    }

    @Test
    public void shouldGetPlayerById() {
        Player playerFound = playerService.getPlayerById(PLAYER_NAME.concat("5"));

        assertThat(playerFound).isEqualTo(createTestPlayer(PLAYER_NAME.concat("5")));
    }

    @Test
    public void shouldThrowExceptionDueToNullNameOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(createTestPlayer(null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToEmptyNameOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(createTestPlayer("")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToNullPasswordOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(createTestPlayer(PLAYER_NAME.concat("7"), null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToEmptyPasswordOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(createTestPlayer(PLAYER_NAME.concat("7"), "")))
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
        assertThat(playerService.deletePlayer(PLAYER_NAME.concat("6"))).isTrue();
    }

    @Test
    public void shouldThrowExceptionDueToPlayerNotFoundOnPlayerDeletion() {
        assertThatThrownBy(()-> playerService.deletePlayer(PLAYER_NAME.concat("7")))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(String.format("Player with name %s does not exist in the database.", PLAYER_NAME.concat("7")));
    }
}
