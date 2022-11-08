package net.pschab.chessserver.repository;

import net.pschab.chessserver.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.NoSuchElementException;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlayerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    PlayerRepository playerRepository;

    @Test
    public void shouldSaveNewPlayer() {
        Player playerToStore = new Player("Player1a", "password123");
        long initialSize = getPlayerCount();
        playerRepository.save(playerToStore);
        long finalSize = getPlayerCount();

        Player playerFound = playerRepository.findById(playerToStore.getName()).orElse(null);

        assertThat(playerFound).isEqualTo(playerToStore);
        assertThat(finalSize-initialSize).isEqualTo(1);
    }

    private long getPlayerCount() {
        return StreamSupport.stream(playerRepository.findAll().spliterator(), false).count();
    }

    @Test
    public void shouldUpdatePlayerPassword() {
        Player playerToUpdate = new Player("Player1c", "password123");
        entityManager.persist(playerToUpdate);
        assertThat(playerRepository.findById(playerToUpdate.getName())).isNotEmpty();

        String newPassword = "newPassword123";
        playerToUpdate.setPassword(newPassword);
        playerRepository.save(playerToUpdate);

        Player playerFound = playerRepository.findById(playerToUpdate.getName())
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("Player with name %s not found in the database.", playerToUpdate.getName())));

        assertThat(playerFound.getPassword()).isEqualTo(newPassword);

    }

    @Test
    public void shouldDeletePlayer() {
        Player player = new Player("Player1b", "password123");
        entityManager.persist(player);
        assertThat(playerRepository.findById(player.getName())).isNotEmpty();

        playerRepository.delete(player);
        assertThat(playerRepository.findById(player.getName())).isEmpty();
    }
}
