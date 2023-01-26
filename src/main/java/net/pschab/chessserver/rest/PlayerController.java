package net.pschab.chessserver.rest;

import net.pschab.chessserver.entity.Player;
import net.pschab.chessserver.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static net.pschab.chessserver.util.HashEncoder.matches;

@RestController
@RequestMapping("player")
public class PlayerController {

    @Autowired
    PlayerService playerService;

    //TODO add security
    //TODO establish maturity level 4

    @GetMapping()
    public ResponseEntity<List<Player>> getAllPlayers() {
        List<Player> players = playerService.getAllPlayers();
        if (players.isEmpty()) {
            throw new NoSuchElementException("There are no players in the database.");
        } else {
            return new ResponseEntity<>(players, HttpStatus.OK);
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<Player> getById(@PathVariable("name") String name) {
        Optional<Player> playerOptional = playerService.getPlayerById(name);
        if (playerOptional.isPresent()) {
            return new ResponseEntity<>(playerOptional.get(), HttpStatus.OK);
        } else {
            throw new NoSuchElementException(getNoSuchPlayerMessage(name));
        }
    }

    private String getNoSuchPlayerMessage(String name) {
        return "There is no player with name: ".concat(name).concat(" in the database.");
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> addNewPlayer(@RequestBody Player player) {
        playerService.addNewPlayer(player);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.CREATED);
        apiResponse.setMessage(String.format("Player with name: %s created.", player.getName()));
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }

    @PutMapping("/{name}")
    public ResponseEntity<ApiResponse> replacePlayer(@RequestBody Player player, @PathVariable String name) {
        if (!name.equals(player.getName())) {
            throw new IllegalArgumentException("Inconsistent player name value provided as service variable.");
        }
        Optional<Player> optional = playerService.getPlayerById(name);
        Player playerInDb = optional.orElseThrow(() -> new NoSuchElementException(getNoSuchPlayerMessage(name)));
        if (!matches(player.getPassword(), playerInDb.getPassword()) || !playerInDb.getRole().equals(player.getRole()) ) {
            if (playerService.updatePlayer(player)) {
                ApiResponse apiResponse = new ApiResponse(HttpStatus.OK);
                apiResponse.setMessage(String.format("Player with name: %s modified.", player.getName()));
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            }
            else {
                throw new IllegalArgumentException("Unidentified service call error.");
            }
        }
        throw new IllegalArgumentException("Nothing to modify: provided player data are identical to data in the database.");
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deletePlayer(@PathVariable("name") String name) {
        boolean status = playerService.deletePlayer(name);
        if (status) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new NoSuchElementException(getNoSuchPlayerMessage(name));
        }
    }
}
