package net.pschab.chessserver.service;

import net.pschab.chessserver.model.Player;
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

    public Optional<Player> getPlayerByName(String name) {
        playerValidator.validatePlayerName(name);
        try {
            return playerRepository.findById(name);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public boolean addNewPlayer(Player player) {
        validatePlayer(player);
        if (playerRepository.findById(player.getName()).isPresent()) {
            throw new DuplicateKeyException(String.format("Player with name %s already exists.", player.getName()));
        }
        player.setPassword(encode(player.getPassword()));
        playerRepository.save(player);
        return true;
    }

    public boolean updatePlayer(Player player) {
        validatePlayer(player);
        if (playerRepository.findById(player.getName()).isPresent()) {
            player.setPassword(encode(player.getPassword()));
            playerRepository.save(player);
            return true;
        }
        return false;
    }

    private void validatePlayer(Player player) {
        playerValidator.validatePlayerName(player.getName());
        playerValidator.validatePlayerPassword(player.getPassword());
    }

    public boolean deletePlayer(String name) {
        Player player = playerRepository.findById(name).orElseThrow(() ->
                new NoSuchElementException(String.format("Player with name %s does not exist in the database.", name)));
        playerRepository.delete(player);
        return true;
    }
}

