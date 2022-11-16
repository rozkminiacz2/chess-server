package net.pschab.chessserver.service;

import net.pschab.chessserver.entity.Player;
import net.pschab.chessserver.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PlayerServiceTest {

    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    PlayerService playerService;

    @Test
    public void shouldGetAllPlayers() {
        playerService.getAllPlayers();
    }

    @Test
    public void shouldAddNewPlayer() {
        Player playerToStore = new Player("PlayerServiceTest1", "PasswordServiceTest1");
        makeSurePlayerDoesNotExist(playerToStore.getName());
        long initialSize = playerService.getAllPlayers().size();
        playerService.addNewPlayer(playerToStore);
        long finalSize = playerService.getAllPlayers().size();

        assertThat(finalSize-initialSize).isEqualTo(1L);
        assertThat(playerService.getPlayerById(playerToStore.getName())).isNotNull();
    }

    private void makeSurePlayerDoesNotExist(String name) {
        playerRepository.findById(name).ifPresent(playerToDelete -> playerRepository.delete(playerToDelete));
    }

    @Test
    public void shouldGetPlayerById() {
        Player playerToGet = new Player("PlayerServiceTest2", "PasswordServiceTest2");
        makeSurePlayerDoesExist(playerToGet);

        Player playerFound = playerService.getPlayerById(playerToGet.getName());

        assertThat(playerFound).isEqualTo(playerToGet);
    }

    private void makeSurePlayerDoesExist(Player playerToCheck) {
        Player playerFromDatabase = playerRepository.findById(playerToCheck.getName()).orElse(null);
        if (playerFromDatabase == null) {
            playerRepository.save(playerToCheck);
        }
    }

    @Test
    public void shouldThrowExceptionDueToNullNameOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(new Player(null, "PasswordServiceTest25")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToEmptyNameOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(new Player("", "PasswordServiceTest26")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToNullPasswordOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(new Player("PlayerServiceTest3", null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToEmptyPasswordOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(new Player("PlayerServiceTest4", "")))
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
        Player playerToDelete = new Player("PlayerServiceTest5", "PasswordServiceTest5");
        makeSurePlayerDoesExist(playerToDelete);

        assertThat(playerService.deletePlayer("PlayerServiceTest5")).isTrue();
    }

    @Test
    public void shouldThrowExceptionDueToPlayerNotFoundOnPlayerDeletion() {
        Player playerToDelete = new Player("PlayerServiceTest6", "PasswordServiceTest6");
        makeSurePlayerDoesNotExist(playerToDelete.getName());

        assertThatThrownBy(()-> playerService.getPlayerById(playerToDelete.getName()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(String.format("Player with name %s not found in the database.", playerToDelete.getName()));
    }
}
