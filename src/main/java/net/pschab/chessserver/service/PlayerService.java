package net.pschab.chessserver.service;

import net.pschab.chessserver.entity.Player;
import net.pschab.chessserver.repository.PlayerRepository;
import net.pschab.chessserver.util.PlayerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.pschab.chessserver.util.HashEncoder.encode;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerValidator playerValidator;

    public List<Player> getAllPlayers() {
        return StreamSupport.stream(playerRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Optional<Player> getPlayerById(String name) {
        playerValidator.validatePlayerName(name);
        try {
            return playerRepository.findById(name);
        }
        catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public boolean addNewPlayer(Player player) {
        Player playerToStore = getValidatedPlayer(player);
        if (playerRepository.findById(player.getName()).isPresent()) {
            throw new DuplicateKeyException(String.format("Player with name %s already exists.", player.getName()));
        }
        playerToStore.setPassword(encode(playerToStore.getPassword()));
        playerRepository.save(playerToStore);
        return true;
    }

    public boolean updatePlayer(Player player) {
        Player playerToStore = getValidatedPlayer(player);
        if (playerRepository.findById(player.getName()).isPresent()) {
            playerToStore.setPassword(encode(player.getPassword()));
            playerToStore.setRole(player.getRole());
            playerRepository.save(playerToStore);
            return true;
        }
        return false;
    }

    private Player getValidatedPlayer(Player player) {
        playerValidator.validatePlayerName(player.getName());
        playerValidator.validatePlayerPassword(player.getPassword());
        return new Player(player.getName(), player.getPassword(), player.getRole());
    }

    public boolean deletePlayer(String name) {
        Player player = playerRepository.findById(name).orElseThrow(() ->
                new NoSuchElementException(String.format("Player with name %s does not exist in the database.", name)));
        playerRepository.delete(player);
        return true;
    }
}

