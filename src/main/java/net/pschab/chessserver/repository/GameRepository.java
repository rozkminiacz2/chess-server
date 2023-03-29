package net.pschab.chessserver.repository;

import net.pschab.chessserver.model.Game;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<Game, Integer> {

    @Query("SELECT game FROM Game game WHERE game.host.name=?1")
    public List<Game> findHostedByPlayer(String hostName);

    @Query("SELECT game FROM Game game WHERE game.guest.name=?1")
    public List<Game> findGuestedByPlayer(String guestName);
}
