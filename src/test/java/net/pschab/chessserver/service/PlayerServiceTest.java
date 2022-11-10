package net.pschab.chessserver.service;

import net.pschab.chessserver.entity.Player;
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
    PlayerService playerService;

    @Test
    public void shouldGetAllPlayers() {
        playerService.getAllPlayers();
    }

    @Test
    public void shouldAddNewPlayer() {
        //TODO add verification if Players2 already exists OR initialize tests with empty db
        long initialSize = playerService.getAllPlayers().size();
        playerService.addNewPlayer(new Player("Player2", "1234"));
        long finalSize = playerService.getAllPlayers().size();

        assertThat(finalSize-initialSize).isEqualTo(1L);
        assertThat(playerService.getPlayerById("Player2")).isNotNull();
    }

    @Test
    public void shouldThrowExceptionDueToNullNameOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(new Player(null, "1234")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToEmptyNameOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(new Player("", "1234")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToNullPasswordOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(new Player("Player2a", null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToEmptyPasswordOnAddingNewPlayer() {
        assertThatThrownBy(()-> playerService.addNewPlayer(new Player("Player2a", "")))
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
        playerService.addNewPlayer(new Player("Player3", "qwerty7789"));
        assertThat(playerService.getPlayerById("Player3")).isNotNull();
        assertThat(playerService.deletePlayer("Player3")).isTrue();
    }

    @Test
    public void shouldThrowExceptionDueToPlayerNotFoundOnPlayerDeletion() {
        assertThatThrownBy(()-> playerService.getPlayerById("Player4"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Player with name Player4 not found in the database.");
    }
}
