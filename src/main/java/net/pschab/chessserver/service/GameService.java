package net.pschab.chessserver.service;

import net.pschab.chessserver.model.Game;
import net.pschab.chessserver.repository.GameRepository;
import net.pschab.chessserver.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public List<Game> getAllGames() {
        return StreamSupport.stream(gameRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Game> getAllGamesHostedByPlayer(String hostName) {
        return StreamSupport.stream(gameRepository.findHostedByPlayer(hostName).spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Game> getAllGamesGuestedByPlayer(String guestName) {
        return StreamSupport.stream(gameRepository.findGuestedByPlayer(guestName).spliterator(), false)
                .collect(Collectors.toList());
    }

    public Optional<Game> getGameById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Game id cannot be null.");
        }
        try {
            return gameRepository.findById(id);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public boolean addNewGame(Game game) {
        validateHostAndGuest(game);
        validateGame(game);
        gameRepository.save(game);
        return true;
    }

    private void validateHostAndGuest(Game game) {
        playerRepository.findById(game.getHost().getName()).orElseThrow(() -> new NoSuchElementException("Host player not found in the database."));
        playerRepository.findById(game.getGuest().getName()).orElseThrow(() -> new NoSuchElementException("Guest player not found in the database."));
    }

    private void validateGame(Game game) {
        if (game.getId() != null) {
            throw new IllegalArgumentException("Game id must be null as it is a generated value.");
        }
        if (game.getHost().equals(game.getGuest())) {
            throw new IllegalArgumentException("Game host cannot be same as game guest.");
        }
    }

    public boolean addMoveToGameHistory(Integer id, String move) {
        return false;
    }

    public boolean updateGame(Game game) {
        if (gameRepository.findById(game.getId()).isPresent()) {
            gameRepository.save(game);
            return true;
        }
        return false;
    }

    public boolean deleteGame(Integer id) {
        Game game = gameRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("Game with id %s does not exist in the database.", id)));
        gameRepository.delete(game);
        return true;
    }

}
