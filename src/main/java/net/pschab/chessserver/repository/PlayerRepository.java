package net.pschab.chessserver.repository;

import net.pschab.chessserver.entity.Player;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlayerRepository extends PagingAndSortingRepository<Player,String> {

}
