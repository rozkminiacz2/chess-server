package net.pschab.chessserver.rest;

import net.pschab.chessserver.entity.Player;
import net.pschab.chessserver.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(players, HttpStatus.OK);
        }
    }

    //TODO add response message to NOT_FOUND scenario

    @GetMapping("/{name}")
    public ResponseEntity<Player> getById(@PathVariable("name") String name) {
        Optional<Player> playerOptional = playerService.getPlayerById(name);
        if (playerOptional.isPresent()) {
            return new ResponseEntity<>(playerOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<Boolean> addNewPlayer(@RequestBody Player player) {
        boolean status = playerService.addNewPlayer(player);
        if (status) {
            return new ResponseEntity<>(true, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    //TODO add response message to NOT_FOUND scenario
    //TODO add response message to BAD_REQUEST scenario
    @PutMapping("/{name}")
    public ResponseEntity<Boolean> replacePlayer(@RequestBody Player player, @PathVariable String name) {
        if (!name.equals(player.getName())) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
        Optional<Player> optional = playerService.getPlayerById(name);
        Player playerInDb = optional.orElseThrow(() -> new NoSuchElementException("No player with name: ".concat(name)));
        //TODO ten warunek równości haseł nie działa - zawsze widzi różne hasła
        if (!playerInDb.getPassword().equals(player.getPassword()) || !playerInDb.getRole().equals(player.getRole()) ) {
            if (playerService.updatePlayer(player)) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(true, HttpStatus.NOT_MODIFIED);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Boolean> deletePlayer(@PathVariable("name") String name) {
        boolean status = playerService.deletePlayer(name);
        if (status) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}
