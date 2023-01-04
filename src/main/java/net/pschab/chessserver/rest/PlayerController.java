package net.pschab.chessserver.rest;

import net.pschab.chessserver.entity.Player;
import net.pschab.chessserver.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
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

    //TODO change to Optional return value
    //TODO adjust ERROR message status and response message
    //TODO change to /person/{name}
    @GetMapping("/get-by-id")
    public ResponseEntity<Player> getById(@PathParam("name") String name) {
        Optional<Player> playerOptional = playerService.getPlayerById(name);
        if (playerOptional.isPresent()) {
            return new ResponseEntity<>(playerOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Boolean> addNewPlayer(@RequestBody Player player) {
        boolean status = playerService.addNewPlayer(player);
        if (status) {
            return new ResponseEntity<>(true, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<Boolean> changePassword(@RequestBody Player player) {
        boolean status = playerService.changePassword(player);
        if (status) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/change-role")
    public ResponseEntity<Boolean> changeRole(@RequestBody Player player) {
        boolean status = playerService.changeRole(player);
        if (status) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/delete")
    public ResponseEntity<Boolean> deletePlayer(@PathParam("name") String name) {
        boolean status = playerService.deletePlayer(name);
        if (status) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
