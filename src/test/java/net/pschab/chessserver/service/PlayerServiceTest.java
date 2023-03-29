package net.pschab.chessserver.service;

import net.pschab.chessserver.model.Player;
import net.pschab.chessserver.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static net.pschab.chessserver.TestPlayerHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PlayerServiceTest {

    private final String playerName = PLAYER_NAME.concat("1");

    @MockBean
    PlayerRepository playerRepository;
    @Autowired
    PlayerService playerService;

    @Test
    public void shouldGetAllPlayers() {
        when(playerRepository.findAll()).thenReturn(getThreePlayerList());

        List<Player> playerList = playerService.getAllPlayers();

        assertThat(playerList).isNotNull().isNotEmpty().hasSize(3);
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("1")));
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("2")));
        assertThat(playerList).contains(createTestPlayer(PLAYER_NAME.concat("3")));
    }

    @Test
    public void shouldAddNewPlayerWithNoRoleSpecified() {
        Boolean status = playerService.addNewPlayer(createTestPlayer(playerName));

        assertThat(status).isTrue();
    }

    @Test
    public void shouldThrowExceptionDueToPlayerWithGivenNameAlreadyExistsInDatabase() {
        when(playerRepository.findById(playerName))
                .thenReturn(Optional.of(createTestPlayer(playerName)));

        assertThatThrownBy(() -> playerService.addNewPlayer(createTestPlayer(playerName)))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage(String.format("Player with name %s already exists.", playerName));
    }

    @Test
    public void shouldExecutePlayerUpdateOperation() {
        when(playerRepository.findById(playerName))
                .thenReturn(Optional.of(createTestPlayer(playerName)));

        assertThat(playerService.updatePlayer(createTestPlayer(playerName))).isEqualTo(true);
    }

    @Test
    public void shouldThrowExceptionOnPlayerUpdateDueToNoPlayerExists() {
        when(playerRepository.findById(playerName))
                .thenReturn(Optional.empty());

        assertThat(playerService.updatePlayer(createTestPlayer(playerName))).isEqualTo(false);
    }

    @Test
    public void shouldGetPlayerById() {
        when(playerRepository.findById(playerName))
                .thenReturn(Optional.of(createTestPlayer(playerName)));

        Optional<Player> playerOptional = playerService.getPlayerByName(playerName);

        assertThat(playerOptional.orElse(null)).isEqualTo(createTestPlayer(playerName));
    }

    @Test
    public void shouldThrowExceptionDueToNullNameOnAddingNewPlayer() {
        assertThatThrownBy(() -> playerService.addNewPlayer(createTestPlayer(null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToEmptyNameOnAddingNewPlayer() {
        assertThatThrownBy(() -> playerService.addNewPlayer(createTestPlayer("")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToNullPasswordOnAddingNewPlayer() {
        assertThatThrownBy(() -> playerService.addNewPlayer(createTestPlayerWithNullPassword(playerName)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player password cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToEmptyPasswordOnAddingNewPlayer() {
        assertThatThrownBy(() -> playerService.addNewPlayer(createTestPlayer(playerName, "")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player password cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToNullNameOnGettingPlayerById() {
        assertThatThrownBy(() -> playerService.getPlayerByName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToEmptyNameOnGettingPlayerById() {
        assertThatThrownBy(() -> playerService.getPlayerByName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldDeletePlayer() {
        when(playerRepository.findById(playerName))
                .thenReturn(Optional.of(createTestPlayer(playerName)));

        assertThat(playerService.deletePlayer(playerName)).isTrue();
    }

    @Test
    public void shouldThrowExceptionDueToPlayerNotFoundOnDeletion() {
        assertThatThrownBy(() -> playerService.deletePlayer(playerName))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(String.format("Player with name %s does not exist in the database.", playerName));
    }
}
