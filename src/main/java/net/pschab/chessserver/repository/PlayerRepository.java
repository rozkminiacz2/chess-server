package net.pschab.chessserver.repository;

import net.pschab.chessserver.model.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, String> {

}
