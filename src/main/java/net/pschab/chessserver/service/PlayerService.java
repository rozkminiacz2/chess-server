package net.pschab.chessserver.service;

import net.pschab.chessserver.entity.Player;
import net.pschab.chessserver.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.pschab.chessserver.util.HashEncoder.encode;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public List<Player> getAllPlayers() {
        return StreamSupport.stream(playerRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Player getPlayerById(String name) { //TODO Preconditions
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be empty.");
        }
        return playerRepository.findById(name).orElseThrow(() -> new NoSuchElementException(
                String.format("Player with name %s not found in the database.", name)));
    }

    public boolean addNewPlayer(Player player) {
        Player playerToStore = getValidatedPlayer(player);
        playerToStore.setPassword(encode(playerToStore.getPassword()));
        playerRepository.save(playerToStore);
        return true;
    }

    private Player getValidatedPlayer(Player player) {
        if (player.getName()==null || player.getName().isBlank()) {
            throw new IllegalArgumentException("Player name cannot be empty.");
        }
        if (player.getPassword()==null || player.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        return new Player(player.getName(), player.getPassword(), player.getRole());
    }

    public boolean deletePlayer(String name) {
        Player player = playerRepository.findById(name)
                .orElseThrow(()->new NoSuchElementException(String.format("Player with name %s does not exist in the database.", name)));
        playerRepository.delete(player);
        return true;
    }
}
