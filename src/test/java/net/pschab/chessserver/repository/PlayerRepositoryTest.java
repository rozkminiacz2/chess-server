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

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlayerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    PlayerRepository playerRepository;

    @Test
    public void shouldSaveNewPlayer() {
        Player playerToStore = new Player("PlayerRepositoryTest1", "PlayerRepositoryPassword1");
        makeSurePlayerDoesNotExist(playerToStore.getName());

        long initialSize = getPlayerCount();
        playerRepository.save(playerToStore);
        long finalSize = getPlayerCount();
        Player playerFound = entityManager.find(Player.class, playerToStore.getName());

        assertThat(playerFound).isEqualTo(playerToStore);
        assertThat(finalSize-initialSize).isEqualTo(1);
    }

    private void makeSurePlayerDoesNotExist(String name) {
        Player playerToDelete = entityManager.find(Player.class, name);
        if (playerToDelete != null) {
            entityManager.remove(playerToDelete);
        }
    }

    private long getPlayerCount() {
        return StreamSupport.stream(playerRepository.findAll().spliterator(), false).count();
    }

    @Test
    public void shouldGetPlayerById() {
        Player playerToGet = new Player("PlayerRepositoryTest2", "PlayerRepositoryPassword2");
        makeSurePlayerDoesExist(playerToGet);

        Player playerFound = playerRepository.findById(playerToGet.getName()).orElse(null);

        assertThat(playerFound).isEqualTo(playerToGet);
    }

    private void makeSurePlayerDoesExist(Player playerToCheck) {
        Player playerFromDatabase = entityManager.find(Player.class, playerToCheck.getName());
        if (playerFromDatabase == null) {
            entityManager.persist(playerToCheck);
        }
    }

    @Test
    public void shouldUpdatePlayerPassword() {
        Player playerToUpdate = new Player("PlayerRepositoryTest3", "PlayerRepositoryPassword3");
        makeSurePlayerDoesExist(playerToUpdate);

        String newPassword = "PlayerRepositoryPassword3a";
        playerToUpdate.setPassword(newPassword);
        playerRepository.save(playerToUpdate);

        Player playerFound = playerRepository.findById(playerToUpdate.getName())
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("Player with name %s not found in the database.", playerToUpdate.getName())));

        assertThat(playerFound.getPassword()).isEqualTo(newPassword);
    }

    @Test
    public void shouldDeletePlayer() {
        Player player = new Player("PlayerRepositoryTest4", "PlayerRepositoryPassword4");
        makeSurePlayerDoesExist(player);

        playerRepository.delete(player);

        assertThat(playerRepository.findById(player.getName())).isEmpty();
    }
}
