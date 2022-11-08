package net.pschab.chessserver.service;

import net.pschab.chessserver.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

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
    public void shouldThrowExceptionDueToNullName() {
        assertThatThrownBy(()-> playerService.addNewPlayer(new Player(null, "1234")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldThrowExceptionDueToEmptyName() {
        assertThatThrownBy(()-> playerService.addNewPlayer(new Player("", "1234")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldDeletePlayer() {
        playerService.addNewPlayer(new Player("Player3", "qwerty7789"));

        assertThat(playerService.deletePlayer("Player3")).isTrue();
    }
}
